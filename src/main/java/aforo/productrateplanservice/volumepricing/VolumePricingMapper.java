package aforo.productrateplanservice.volumepricing;

import aforo.productrateplanservice.rate_plan.RatePlan;
import org.springframework.stereotype.Component;

@Component
public class VolumePricingMapper {

    public VolumePricing toEntity(VolumePricingCreateUpdateDTO dto, RatePlan ratePlan) {
        return VolumePricing.builder()
                .ratePlan(ratePlan)
                .startRange(dto.getStartRange())
                .endRange(dto.getEndRange())
                .unitPrice(dto.getUnitPrice())
                .volumeBracket(dto.getVolumeBracket())
                .overageUnitRate(dto.getOverageUnitRate())   // ✅ new
                .graceBuffer(dto.getGraceBuffer())           // ✅ new
                .build();
    }

    public VolumePricingDTO toDTO(VolumePricing entity) {
        return VolumePricingDTO.builder()
                .volumePricingId(entity.getVolumePricingId())
                .ratePlanId(entity.getRatePlan().getRatePlanId())
                .startRange(entity.getStartRange())
                .endRange(entity.getEndRange())
                .unitPrice(entity.getUnitPrice())
                .volumeBracket(entity.getVolumeBracket())
                .overageUnitRate(entity.getOverageUnitRate())  // ✅ new
                .graceBuffer(entity.getGraceBuffer())          // ✅ new
                .build();
    }

    public void updateEntity(VolumePricing entity, VolumePricingCreateUpdateDTO dto) {
        entity.setStartRange(dto.getStartRange());
        entity.setEndRange(dto.getEndRange());
        entity.setUnitPrice(dto.getUnitPrice());
        entity.setVolumeBracket(dto.getVolumeBracket());

        if (dto.getOverageUnitRate() != null) {
            entity.setOverageUnitRate(dto.getOverageUnitRate());  // ✅ new
        }

        if (dto.getGraceBuffer() != null) {
            entity.setGraceBuffer(dto.getGraceBuffer());          // ✅ new
        }
    }
}
