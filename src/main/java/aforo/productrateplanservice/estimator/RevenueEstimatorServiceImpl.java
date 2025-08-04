package aforo.productrateplanservice.estimator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Stateless Revenue Estimator implementation.
 * NO DB or repository dependencies.
 * All input comes from EstimateRequest (UI/REST).
 */
@Service
@RequiredArgsConstructor
public class RevenueEstimatorServiceImpl implements RevenueEstimatorService {

    @Override
    public EstimateResponse estimate(EstimateRequest request) {
        if (request == null || request.getPricingModel() == null)
            throw new IllegalArgumentException("Request and pricing model must be provided");

        List<EstimateResponse.LineItem> lineItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        int usage = request.getUsageSafe();

        // -- Base Pricing Models --
        switch (request.getPricingModel()) {
            case FLATFEE -> {
                BigDecimal base = request.getFlatFeeAmountSafe();
                lineItems.add(EstimateResponse.LineItem.builder()
                        .label("Flat Fee")
                        .calculation("Base flat fee")
                        .amount(base)
                        .build());
                total = total.add(base);

                int includedUnits = request.getIncludedUnitsSafe();
                BigDecimal overRate = request.getOverageUnitRateSafe();
                int overUnits = Math.max(0, usage - includedUnits);
                if (overUnits > 0 && overRate.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal overAmount = overRate.multiply(BigDecimal.valueOf(overUnits));
                    lineItems.add(EstimateResponse.LineItem.builder()
                            .label("Overage Charges")
                            .calculation(overUnits + " * " + overRate)
                            .amount(overAmount)
                            .build());
                    total = total.add(overAmount);
                }
            }
            case USAGE_BASED -> {
                BigDecimal per = request.getPerUnitAmountSafe();
                BigDecimal usageAmt = per.multiply(BigDecimal.valueOf(usage));
                lineItems.add(EstimateResponse.LineItem.builder()
                        .label("Usage Charges")
                        .calculation(per + " * " + usage)
                        .amount(usageAmt)
                        .build());
                total = total.add(usageAmt);
            }
            case TIERED -> {
                if (request.getTiers() != null && !request.getTiers().isEmpty()) {
                    List<EstimateRequest.Tier> tiers = new ArrayList<>(request.getTiers());
                    tiers.sort(Comparator.comparingInt(EstimateRequest.Tier::getMinUnits));
                    int remaining = usage;
                    for (EstimateRequest.Tier tier : tiers) {
                        int min = tier.getMinUnits() != null ? tier.getMinUnits() : 0;
                        int max = tier.getMaxUnits() != null ? tier.getMaxUnits() : Integer.MAX_VALUE;
                        if (remaining <= 0) break;
                        int bandUnits = Math.min(remaining, max - min + 1);
                        if (usage >= min) {
                            BigDecimal seg = tier.getPricePerUnit().multiply(BigDecimal.valueOf(bandUnits));
                            lineItems.add(EstimateResponse.LineItem.builder()
                                    .label("Tier " + min + "-" + max)
                                    .calculation(bandUnits + " * " + tier.getPricePerUnit())
                                    .amount(seg)
                                    .build());
                            total = total.add(seg);
                            remaining -= bandUnits;
                        }
                    }
                }
            }
            case VOLUME_BASED -> {
                if (request.getTiers() != null && !request.getTiers().isEmpty()) {
                    List<EstimateRequest.Tier> vols = new ArrayList<>(request.getTiers());
                    vols.sort(Comparator.comparingInt(EstimateRequest.Tier::getMinUnits));
                    EstimateRequest.Tier matched = vols.stream()
                            .filter(v -> usage >= v.getMinUnits() &&
                                    (v.getMaxUnits() == null || usage <= v.getMaxUnits()))
                            .findFirst()
                            .orElse(vols.get(vols.size() - 1));
                    BigDecimal volCharge = matched.getPricePerUnit().multiply(BigDecimal.valueOf(usage));
                    lineItems.add(EstimateResponse.LineItem.builder()
                            .label("Volume Charge")
                            .calculation(usage + " * " + matched.getPricePerUnit())
                            .amount(volCharge)
                            .build());
                    total = total.add(volCharge);
                }
            }
            case STAIRSTEP -> {
                if (request.getSteps() != null && !request.getSteps().isEmpty()) {
                    List<EstimateRequest.Step> steps = new ArrayList<>(request.getSteps());
                    steps.sort(Comparator.comparingInt(EstimateRequest.Step::getUsageThresholdStart));
                    EstimateRequest.Step chosen = null;
                    for (EstimateRequest.Step s : steps) {
                        int start = s.getUsageThresholdStart() != null ? s.getUsageThresholdStart() : 0;
                        int end = s.getUsageThresholdEnd() != null ? s.getUsageThresholdEnd() : Integer.MAX_VALUE;
                        if (usage >= start && usage <= end) {
                            chosen = s;
                            break;
                        }
                    }
                    if (chosen == null) {
                        chosen = steps.get(steps.size() - 1);
                    }
                    BigDecimal stairCharge = chosen.getMonthlyCharge() != null ? chosen.getMonthlyCharge() : BigDecimal.ZERO;
                    lineItems.add(EstimateResponse.LineItem.builder()
                            .label("Stair Step Charge")
                            .calculation((chosen.getUsageThresholdStart() + "-" +
                                    (chosen.getUsageThresholdEnd() != null ? chosen.getUsageThresholdEnd() : "∞")))
                            .amount(stairCharge)
                            .build());
                    total = total.add(stairCharge);
                }
            }
        }

        // -- Extras & Adjustments --
        // Setup Fee
        if (Boolean.TRUE.equals(request.getIncludeSetup()) && request.getSetupFee() != null) {
            total = total.add(request.getSetupFee());
            lineItems.add(EstimateResponse.LineItem.builder()
                    .label("Setup Fee")
                    .calculation("Fixed")
                    .amount(request.getSetupFee())
                    .build());
        }

        // Freemium
        if (Boolean.TRUE.equals(request.getIncludeFreemium()) && request.getFreeUnits() != null) {
            int freeUnits = request.getFreeUnits();
            BigDecimal perUnit = request.getPerUnitAmountSafe();
            BigDecimal credit = perUnit.multiply(BigDecimal.valueOf(Math.min(freeUnits, usage)));
            if (credit.compareTo(BigDecimal.ZERO) > 0) {
                total = total.subtract(credit);
                lineItems.add(EstimateResponse.LineItem.builder()
                        .label("Freemium Credit")
                        .calculation(freeUnits + " free units")
                        .amount(credit.negate())
                        .build());
            }
        }

        // Minimum Commitment
        if (Boolean.TRUE.equals(request.getIncludeCommitment()) && request.getMinCommitmentAmount() != null) {
            BigDecimal minAmt = request.getMinCommitmentAmount();
            if (total.compareTo(minAmt) < 0) {
                BigDecimal diff = minAmt.subtract(total);
                total = minAmt;
                lineItems.add(EstimateResponse.LineItem.builder()
                        .label("Minimum Commitment Uplift")
                        .calculation("Adjusted to minimum")
                        .amount(diff)
                        .build());
            }
        }

        // Discount
        if (Boolean.TRUE.equals(request.getIncludeDiscount())) {
            BigDecimal discountAmt = BigDecimal.ZERO;
            String label = "Discount";
            if (request.getDiscountPct() != null && request.getDiscountPct().compareTo(BigDecimal.ZERO) > 0) {
                discountAmt = total.multiply(request.getDiscountPct())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                label = "Discount (" + request.getDiscountPct() + "%)";
            } else if (request.getFlatDiscountAmount() != null && request.getFlatDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
                discountAmt = request.getFlatDiscountAmount();
                label = "Flat Discount";
            }
            if (discountAmt.compareTo(BigDecimal.ZERO) > 0) {
                total = total.subtract(discountAmt);
                lineItems.add(EstimateResponse.LineItem.builder()
                        .label(label)
                        .calculation(label)
                        .amount(discountAmt.negate())
                        .build());
            }
        }

        return EstimateResponse.builder()
                .modelType(request.getPricingModel().name())
                .breakdown(lineItems)
                .total(total.setScale(2, RoundingMode.HALF_UP))
                .build();
    }
}
