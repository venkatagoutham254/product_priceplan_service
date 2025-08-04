package aforo.productrateplanservice.estimator;

import aforo.productrateplanservice.enums.RatePlanType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstimateRequest {
    @NotNull(message = "pricingModel is required")
    private RatePlanType pricingModel;

    @PositiveOrZero(message = "usage must be zero or positive")
    private Integer usage; // Number of billable units (API calls, tokens, etc.)

    // Flat Fee
    private BigDecimal flatFeeAmount;
    private Integer numberOfApiCalls;
    private BigDecimal overageUnitRate;

    // Usage Based
    private BigDecimal perUnitAmount;

    // Tiered & Volume Based Pricing
    private List<Tier> tiers;

    // Stair Step Pricing
    private List<Step> steps;

    // Extras
    private Boolean includeSetup;
    private BigDecimal setupFee;

    private Boolean includeDiscount;
    private BigDecimal discountPct;
    private BigDecimal flatDiscountAmount;

    private Boolean includeFreemium;
    private Integer freeUnits;

    private Boolean includeCommitment;
    private BigDecimal minCommitmentAmount;

    // --- Nested DTOs ---
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tier {
        private Integer minUnits;
        private Integer maxUnits;
        private BigDecimal pricePerUnit;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Step {
        private Integer usageThresholdStart;
        private Integer usageThresholdEnd;
        private BigDecimal monthlyCharge;
    }

    // --- Helpers ---
    public int getUsageSafe() {
        return usage != null ? usage : 0;
    }

    public BigDecimal getPerUnitAmountSafe() {
        return perUnitAmount != null ? perUnitAmount : BigDecimal.ZERO;
    }

    public BigDecimal getFlatFeeAmountSafe() {
        return flatFeeAmount != null ? flatFeeAmount : BigDecimal.ZERO;
    }

    public BigDecimal getOverageUnitRateSafe() {
        return overageUnitRate != null ? overageUnitRate : BigDecimal.ZERO;
    }

    public int getIncludedUnitsSafe() {
        return numberOfApiCalls != null ? numberOfApiCalls : 0;
    }
}
