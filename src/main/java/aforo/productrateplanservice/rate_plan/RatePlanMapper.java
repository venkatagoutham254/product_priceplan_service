package aforo.productrateplanservice.rate_plan;

import org.springframework.stereotype.Component;

@Component
public class RatePlanMapper {

    public RatePlanDTO toDTO(RatePlan ratePlan) {
        return RatePlanDTO.builder()
                .ratePlanId(ratePlan.getRatePlanId())
                .ratePlanName(ratePlan.getRatePlanName())
                .description(ratePlan.getDescription())
                .billingFrequency(ratePlan.getBillingFrequency())
                .productId(ratePlan.getProduct() != null ? ratePlan.getProduct().getProductId() : null)
                .productName(ratePlan.getProduct() != null ? ratePlan.getProduct().getProductName() : null)
                .status(ratePlan.getStatus())
                .paymentType(ratePlan.getPaymentType())
                .billableMetricId(ratePlan.getBillableMetricId())
                .createdOn(ratePlan.getCreatedOn())
                .lastUpdated(ratePlan.getLastUpdated())
                .build();
    }
    
}
