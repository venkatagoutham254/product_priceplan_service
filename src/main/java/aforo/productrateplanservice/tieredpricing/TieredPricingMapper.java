package aforo.productrateplanservice.tieredpricing;

import aforo.productrateplanservice.rate_plan.RatePlan;
import org.springframework.stereotype.Component;

@Component
public class TieredPricingMapper {

    public TieredPricing toEntity(TieredPricingCreateUpdateDTO dto, RatePlan ratePlan) {
        return TieredPricing.builder()
                .ratePlan(ratePlan)
                .startRange(dto.getStartRange())
                .endRange(dto.getEndRange())
                .unitPrice(dto.getUnitPrice())
                .tierBracket(dto.getTierBracket())
                .overageUnitRate(dto.getOverageUnitRate())   // ✅ new
                .graceBuffer(dto.getGraceBuffer())           // ✅ new
                .build();
    }

    public TieredPricingDTO toDTO(TieredPricing entity) {
        return TieredPricingDTO.builder()
                .tieredPricingId(entity.getTieredPricingId())
                .ratePlanId(entity.getRatePlan().getRatePlanId())
                .startRange(entity.getStartRange())
                .endRange(entity.getEndRange())
                .unitPrice(entity.getUnitPrice())
                .tierBracket(entity.getTierBracket())
                .overageUnitRate(entity.getOverageUnitRate())   // ✅ new
                .graceBuffer(entity.getGraceBuffer())           // ✅ new
                .build();
    }

    public void updateEntity(TieredPricing entity, TieredPricingCreateUpdateDTO dto) {
        entity.setStartRange(dto.getStartRange());
        entity.setEndRange(dto.getEndRange());
        entity.setUnitPrice(dto.getUnitPrice());
        entity.setTierBracket(dto.getTierBracket());

        if (dto.getOverageUnitRate() != null) {
            entity.setOverageUnitRate(dto.getOverageUnitRate());  // ✅ new
        }

        if (dto.getGraceBuffer() != null) {
            entity.setGraceBuffer(dto.getGraceBuffer());          // ✅ new
        }
    }
}
