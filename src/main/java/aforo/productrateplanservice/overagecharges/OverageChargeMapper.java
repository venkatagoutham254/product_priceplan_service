package aforo.productrateplanservice.overagecharges;

import aforo.productrateplanservice.rate_plan.RatePlan;
import org.springframework.stereotype.Component;

@Component
public class OverageChargeMapper {

    public OverageChargeDTO toDTO(OverageCharge entity) {
        return OverageChargeDTO.builder()
                .id(entity.getId())
                .ratePlanId(entity.getRatePlan().getRatePlanId())
                .overageUnitRate(entity.getOverageUnitRate())
                .graceBuffer(entity.getGraceBuffer())
                .build();
    }

    public OverageCharge toEntity(OverageChargeCreateUpdateDTO dto, RatePlan ratePlan) {
        return OverageCharge.builder()
                .ratePlan(ratePlan)
                .overageUnitRate(dto.getOverageUnitRate())
                .graceBuffer(dto.getGraceBuffer())
                .build();
    }

    public void updateEntity(OverageCharge entity, OverageChargeCreateUpdateDTO dto) {
        entity.setOverageUnitRate(dto.getOverageUnitRate());
        entity.setGraceBuffer(dto.getGraceBuffer());
    }

    public void partialUpdate(OverageCharge entity, OverageChargeCreateUpdateDTO dto) {
        if (dto.getOverageUnitRate() != null) {
            entity.setOverageUnitRate(dto.getOverageUnitRate());
        }
        if (dto.getGraceBuffer() != null) {
            entity.setGraceBuffer(dto.getGraceBuffer());
        }
    }
}
