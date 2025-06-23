package aforo.productrateplanservice.tieredpricing;

import org.springframework.stereotype.Component;

@Component
public class TieredPricingMapper {
    public TieredPricingDTO toDTO(TieredPricing entity) {
        return TieredPricingDTO.builder()
                .id(entity.getId())
                .startRange(entity.getStartRange())
                .endRange(entity.getEndRange())
                .unitPrice(entity.getUnitPrice())
                .tierBracket(entity.getTierBracket())
                .build();
    }
}
