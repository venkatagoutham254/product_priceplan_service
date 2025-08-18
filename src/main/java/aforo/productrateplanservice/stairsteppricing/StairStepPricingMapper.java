package aforo.productrateplanservice.stairsteppricing;

import aforo.productrateplanservice.rate_plan.RatePlan;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class StairStepPricingMapper {

    public StairStepPricing toEntity(StairStepPricingCreateUpdateDTO dto, RatePlan ratePlan) {
        StairStepPricing pricing = StairStepPricing.builder()
                .ratePlan(ratePlan)
                .overageUnitRate(dto.getOverageUnitRate())
                .graceBuffer(dto.getGraceBuffer())
                .build();

        if (dto.getTiers() != null) {
            pricing.setTiers(
                dto.getTiers().stream()
                        .map(t -> StairStepTier.builder()
                                .usageStart(t.getUsageStart())
                                .usageEnd(t.getUsageEnd())
                                .flatCost(t.getFlatCost())
                                .stairStepPricing(pricing)
                                .build()
                        ).collect(Collectors.toList())
            );
        }
        return pricing;
    }

    public StairStepPricingDTO toDTO(StairStepPricing entity) {
        return StairStepPricingDTO.builder()
                .stairStepPricingId(entity.getStairStepPricingId())
                .overageUnitRate(entity.getOverageUnitRate())
                .graceBuffer(entity.getGraceBuffer())
                .tiers(
                        entity.getTiers() != null
                                ? entity.getTiers().stream()
                                    .map(t -> StairStepTierDTO.builder()
                                            .stairStepTierId(t.getStairStepTierId())
                                            .usageStart(t.getUsageStart())
                                            .usageEnd(t.getUsageEnd())
                                            .flatCost(t.getFlatCost())
                                            .build()
                                    ).collect(Collectors.toList())
                                : null
                )
                .build();
    }

    public void updateEntity(StairStepPricing entity, StairStepPricingCreateUpdateDTO dto) {
        // Replace all tiers with new ones
        entity.getTiers().clear();
        if (dto.getTiers() != null) {
            dto.getTiers().forEach(t -> {
                StairStepTier tier = StairStepTier.builder()
                        .usageStart(t.getUsageStart())
                        .usageEnd(t.getUsageEnd())
                        .flatCost(t.getFlatCost())
                        .stairStepPricing(entity)
                        .build();
                entity.getTiers().add(tier);
            });
        }

        if (dto.getOverageUnitRate() != null) {
            entity.setOverageUnitRate(dto.getOverageUnitRate());
        }
        if (dto.getGraceBuffer() != null) {
            entity.setGraceBuffer(dto.getGraceBuffer());
        }
    }
}
