package aforo.productrateplanservice.volumepricing;

import aforo.productrateplanservice.rate_plan.RatePlan;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class VolumePricingMapper {

    public VolumePricing toEntity(VolumePricingCreateUpdateDTO dto, RatePlan ratePlan) {
        VolumePricing volumePricing = VolumePricing.builder()
                .ratePlan(ratePlan)
                .overageUnitRate(dto.getOverageUnitRate())
                .graceBuffer(dto.getGraceBuffer())
                .build();

        if (dto.getTiers() != null) {
            volumePricing.setTiers(
                dto.getTiers().stream()
                        .map(tierDto -> VolumeTier.builder()
                                .usageStart(tierDto.getUsageStart())
                                .usageEnd(tierDto.getUsageEnd())
                                .unitPrice(tierDto.getUnitPrice())
                                .volumePricing(volumePricing) // link back
                                .build())
                        .collect(Collectors.toList())
            );
        }

        return volumePricing;
    }

    public VolumePricingDTO toDTO(VolumePricing entity) {
        return VolumePricingDTO.builder()
                .volumePricingId(entity.getVolumePricingId())
                .ratePlanId(entity.getRatePlan().getRatePlanId())
                .overageUnitRate(entity.getOverageUnitRate())
                .graceBuffer(entity.getGraceBuffer())
                .tiers(
                        entity.getTiers().stream()
                                .map(tier -> VolumeTierDTO.builder()
                                        .volumeTierId(tier.getVolumeTierId())
                                        .usageStart(tier.getUsageStart())
                                        .usageEnd(tier.getUsageEnd())
                                        .unitPrice(tier.getUnitPrice())
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();
    }

    public void updateEntity(VolumePricing entity, VolumePricingCreateUpdateDTO dto) {
        entity.setOverageUnitRate(dto.getOverageUnitRate());
        entity.setGraceBuffer(dto.getGraceBuffer());

        // replace tiers
        if (dto.getTiers() != null) {
            entity.getTiers().clear();
            dto.getTiers().forEach(tierDto -> {
                VolumeTier tier = VolumeTier.builder()
                        .usageStart(tierDto.getUsageStart())
                        .usageEnd(tierDto.getUsageEnd())
                        .unitPrice(tierDto.getUnitPrice())
                        .volumePricing(entity)
                        .build();
                entity.getTiers().add(tier);
            });
        }
    }
}
