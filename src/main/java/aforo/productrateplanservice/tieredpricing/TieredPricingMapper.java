package aforo.productrateplanservice.tieredpricing;

import aforo.productrateplanservice.rate_plan.RatePlan;
import org.springframework.stereotype.Component;

@Component
public class TieredPricingMapper {

    public TieredPricing toEntity(TieredPricingCreateUpdateDTO dto, RatePlan ratePlan) {
        TieredPricing tieredPricing = new TieredPricing();
        tieredPricing.setRatePlan(ratePlan);
        tieredPricing.setOverageUnitRate(dto.getOverageUnitRate());
        tieredPricing.setGraceBuffer(dto.getGraceBuffer());
        if (dto.getTiers() != null) {
            java.util.List<TieredTier> tiers = new java.util.ArrayList<>();
            for (TieredTierCreateUpdateDTO tierDTO : dto.getTiers()) {
                TieredTier tier = toTierEntity(tierDTO, tieredPricing);
                tiers.add(tier);
            }
            tieredPricing.setTiers(tiers);
        }
        return tieredPricing;
    }

    private TieredTier toTierEntity(TieredTierCreateUpdateDTO dto, TieredPricing parent) {
        return TieredTier.builder()
                .startRange(dto.getStartRange())
                .endRange(dto.getEndRange())
                .unitPrice(dto.getUnitPrice())
                .tieredPricing(parent)
                .build();
    }

    public TieredPricingDTO toDTO(TieredPricing entity) {
        java.util.List<TieredTierDTO> tierDTOs = new java.util.ArrayList<>();
        if (entity.getTiers() != null) {
            for (TieredTier tier : entity.getTiers()) {
                tierDTOs.add(toTierDTO(tier));
            }
        }
        return TieredPricingDTO.builder()
                .tieredPricingId(entity.getTieredPricingId())
                .ratePlanId(entity.getRatePlan().getRatePlanId())
                .overageUnitRate(entity.getOverageUnitRate())
                .graceBuffer(entity.getGraceBuffer())
                .tiers(tierDTOs)
                .build();
    }

    private TieredTierDTO toTierDTO(TieredTier tier) {
        return TieredTierDTO.builder()
                .tieredTierId(tier.getTieredTierId())
                .startRange(tier.getStartRange())
                .endRange(tier.getEndRange())
                .unitPrice(tier.getUnitPrice())
                .build();
    }

    public void updateEntity(TieredPricing entity, TieredPricingCreateUpdateDTO dto) {
        entity.setOverageUnitRate(dto.getOverageUnitRate());
        entity.setGraceBuffer(dto.getGraceBuffer());
        // Clear and update tiers
        entity.getTiers().clear();
        if (dto.getTiers() != null) {
            for (TieredTierCreateUpdateDTO tierDTO : dto.getTiers()) {
                TieredTier tier = toTierEntity(tierDTO, entity);
                entity.getTiers().add(tier);
            }
        }
    }
}
