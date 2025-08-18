package aforo.productrateplanservice.tieredpricing;

import aforo.productrateplanservice.enums.RatePlanType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TieredPricingDTO {

    private Long tieredPricingId;

    private Long ratePlanId;

    private java.math.BigDecimal overageUnitRate;
    private Integer graceBuffer;

    private final RatePlanType ratePlanType = RatePlanType.TIERED;

    private java.util.List<TieredTierDTO> tiers;
}
