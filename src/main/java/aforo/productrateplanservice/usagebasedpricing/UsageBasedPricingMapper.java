package aforo.productrateplanservice.usagebasedpricing;

import aforo.productrateplanservice.rate_plan.RatePlan;
import org.springframework.stereotype.Component;

@Component
public class UsageBasedPricingMapper {

    public UsageBasedPricing toEntity(UsageBasedPricingCreateUpdateDTO dto, RatePlan ratePlan) {
        return UsageBasedPricing.builder()
                .ratePlan(ratePlan)
                .perUnitAmount(dto.getPerUnitAmount())
                .build();
    }

    public UsageBasedPricingDTO toDTO(UsageBasedPricing entity) {
        return UsageBasedPricingDTO.builder()
                .usageBasedPricingId(entity.getUsageBasedPricingId())
                .ratePlanId(entity.getRatePlan().getRatePlanId())
                .perUnitAmount(entity.getPerUnitAmount())
                .build();
    }

    public void updateEntity(UsageBasedPricing entity, UsageBasedPricingCreateUpdateDTO dto) {
        entity.setPerUnitAmount(dto.getPerUnitAmount());
    }
}
