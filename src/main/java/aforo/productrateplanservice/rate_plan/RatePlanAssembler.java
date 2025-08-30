package aforo.productrateplanservice.rate_plan;

import org.springframework.stereotype.Component;

import aforo.productrateplanservice.product.enums.RatePlanStatus;
import aforo.productrateplanservice.product.entity.Product;

@Component
public class RatePlanAssembler {
    public RatePlan toEntity(RatePlanDTO dto, Product product) {
        RatePlan.RatePlanBuilder builder = RatePlan.builder()
            .ratePlanName(dto.getRatePlanName())
            .description(dto.getDescription())
            .billingFrequency(dto.getBillingFrequency())
            .status(RatePlanStatus.DRAFT)   // always default
            .paymentType(dto.getPaymentType())
            .billableMetricId(dto.getBillableMetricId());
        
        if (product != null) {
            builder.product(product);
        }

        return builder.build();  // DB handles createdOn & lastUpdated
    }
}