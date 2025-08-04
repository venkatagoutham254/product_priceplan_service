package aforo.productrateplanservice.rate_plan;

import org.springframework.stereotype.Component;

import aforo.productrateplanservice.product.enums.RatePlanStatus;
import aforo.productrateplanservice.product.entity.Product;

@Component
public class RatePlanAssembler {
    public RatePlan toEntity(RatePlanDTO dto, Product product) {
        return RatePlan.builder()
            .ratePlanName(dto.getRatePlanName())
            .description(dto.getDescription())
            .billingFrequency(dto.getBillingFrequency())
            .product(product)  // ✅ Now this is the JPA entity
            .status(RatePlanStatus.DRAFT)
            .paymentType(dto.getPaymentType())
            .billableMetricId(dto.getBillableMetricId())
            .build();
    }
}