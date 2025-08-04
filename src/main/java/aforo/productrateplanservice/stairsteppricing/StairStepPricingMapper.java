package aforo.productrateplanservice.stairsteppricing;

import aforo.productrateplanservice.rate_plan.RatePlan;
import org.springframework.stereotype.Component;

@Component
public class StairStepPricingMapper {

    public StairStepPricing toEntity(StairStepPricingCreateUpdateDTO dto, RatePlan ratePlan) {
        return StairStepPricing.builder()
                .ratePlan(ratePlan)
                .usageThresholdStart(dto.getUsageThresholdStart())
                .usageThresholdEnd(dto.getUsageThresholdEnd())
                .monthlyCharge(dto.getMonthlyCharge())
                .stairBracket(dto.getStairBracket())
                .overageUnitRate(dto.getOverageUnitRate())   // ✅ new
                .graceBuffer(dto.getGraceBuffer())           // ✅ new
                .build();
    }

    public StairStepPricingDTO toDTO(StairStepPricing entity) {
        return StairStepPricingDTO.builder()
                .stairStepPricingId(entity.getStairStepPricingId())
                .usageThresholdStart(entity.getUsageThresholdStart())
                .usageThresholdEnd(entity.getUsageThresholdEnd())
                .monthlyCharge(entity.getMonthlyCharge())
                .stairBracket(entity.getStairBracket())
                .overageUnitRate(entity.getOverageUnitRate())   // ✅ new
                .graceBuffer(entity.getGraceBuffer())           // ✅ new
                .build();
    }

    public void updateEntity(StairStepPricing entity, StairStepPricingCreateUpdateDTO dto) {
        entity.setUsageThresholdStart(dto.getUsageThresholdStart());
        entity.setUsageThresholdEnd(dto.getUsageThresholdEnd());
        entity.setMonthlyCharge(dto.getMonthlyCharge());
        entity.setStairBracket(dto.getStairBracket());

        if (dto.getOverageUnitRate() != null) {
            entity.setOverageUnitRate(dto.getOverageUnitRate());  // ✅ new
        }

        if (dto.getGraceBuffer() != null) {
            entity.setGraceBuffer(dto.getGraceBuffer());          // ✅ new
        }
    }
}
