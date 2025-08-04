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
                .productId(ratePlan.getProduct().getProductId())
                .productName(ratePlan.getProduct().getProductName())
                .status(ratePlan.getStatus())
                .paymentType(ratePlan.getPaymentType())
                .billableMetricId(ratePlan.getBillableMetricId())
                .build();
    }
}
