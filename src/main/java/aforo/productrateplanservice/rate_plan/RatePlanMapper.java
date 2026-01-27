package aforo.productrateplanservice.rate_plan;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import aforo.productrateplanservice.setupfee.SetupFeeRepository;
import aforo.productrateplanservice.setupfee.SetupFeeMapper;
import aforo.productrateplanservice.discount.DiscountRepository;
import aforo.productrateplanservice.discount.DiscountMapper;
import aforo.productrateplanservice.freemium.FreemiumRepository;
import aforo.productrateplanservice.freemium.FreemiumMapper;
import aforo.productrateplanservice.minimumcommitment.MinimumCommitmentRepository;
import aforo.productrateplanservice.minimumcommitment.MinimumCommitmentMapper;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RatePlanMapper {

    private final SetupFeeRepository setupFeeRepository;
    private final SetupFeeMapper setupFeeMapper;
    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;
    private final FreemiumRepository freemiumRepository;
    private final FreemiumMapper freemiumMapper;
    private final MinimumCommitmentRepository minimumCommitmentRepository;
    private final MinimumCommitmentMapper minimumCommitmentMapper;

    public RatePlanDTO toDTO(RatePlan ratePlan) {
        Long ratePlanId = ratePlan.getRatePlanId();
        
        return RatePlanDTO.builder()
                .ratePlanId(ratePlanId)
                .ratePlanCode(ratePlan.getRatePlanCode())
                .ratePlanName(ratePlan.getRatePlanName())
                .description(ratePlan.getDescription())
                .billingFrequency(ratePlan.getBillingFrequency())
                .productId(ratePlan.getProduct() != null ? ratePlan.getProduct().getProductId() : null)
                .productName(ratePlan.getProduct() != null ? ratePlan.getProduct().getProductName() : null)
                .icon(ratePlan.getProduct() != null ? ratePlan.getProduct().getIcon() : null)
                .status(ratePlan.getStatus())
                .paymentType(ratePlan.getPaymentType())
                .billableMetricId(ratePlan.getBillableMetricId())
                .createdOn(ratePlan.getCreatedOn())
                .lastUpdated(ratePlan.getLastUpdated())
                // Fetch and include extras
                .setupFees(setupFeeRepository.findByRatePlan_RatePlanId(ratePlanId)
                        .stream()
                        .map(setupFeeMapper::toDTO)
                        .collect(Collectors.toList()))
                .discounts(discountRepository.findByRatePlan_RatePlanId(ratePlanId)
                        .stream()
                        .map(discountMapper::toDTO)
                        .collect(Collectors.toList()))
                .freemiums(freemiumRepository.findByRatePlan_RatePlanId(ratePlanId)
                        .stream()
                        .map(freemiumMapper::toDTO)
                        .collect(Collectors.toList()))
                .minimumCommitments(minimumCommitmentRepository.findByRatePlan_RatePlanId(ratePlanId)
                        .stream()
                        .map(minimumCommitmentMapper::toDTO)
                        .collect(Collectors.toList()))
                .build();
    }
    
}
