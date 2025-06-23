package aforo.productrateplanservice.stairsteppricing;

import org.springframework.stereotype.Component;

@Component
public class StairStepPricingMapper {

    public StairStepPricingDTO toDTO(StairStepPricing entity) {
        return StairStepPricingDTO.builder()
                .id(entity.getId())
                .usageThresholdStart(entity.getUsageThresholdStart())
                .usageThresholdEnd(entity.getUsageThresholdEnd())
                .monthlyCharge(entity.getMonthlyCharge())
                .stairBracket(entity.getStairBracket())
                .build();
    }
}
