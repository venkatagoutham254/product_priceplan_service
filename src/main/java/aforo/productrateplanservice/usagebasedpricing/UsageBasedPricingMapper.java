package aforo.productrateplanservice.usagebasedpricing;

import aforo.productrateplanservice.rate_plan.RatePlan;
import org.springframework.stereotype.Component;

@Component
public class UsageBasedPricingMapper {

    public UsageBasedPricingDTO toDTO(UsageBasedPricing entity) {
        return UsageBasedPricingDTO.builder()
                .id(entity.getId())
                .ratePlanId(entity.getRatePlan().getRatePlanId())
                .perUnitAmount(entity.getPerUnitAmount())
                .build();
    }

    public UsageBasedPricing toEntity(RatePlan ratePlan, UsageBasedPricingCreateUpdateDTO dto) {
        return UsageBasedPricing.builder()
                .ratePlan(ratePlan)
                .perUnitAmount(dto.getPerUnitAmount())
                .build();
    }

    public void updateEntity(UsageBasedPricing entity, UsageBasedPricingCreateUpdateDTO dto) {
        entity.setPerUnitAmount(dto.getPerUnitAmount());
    }
}
