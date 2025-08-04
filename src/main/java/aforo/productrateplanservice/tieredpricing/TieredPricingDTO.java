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

    private Integer startRange;

    private Integer endRange;

    private BigDecimal unitPrice;

    private String tierBracket;

    private final RatePlanType ratePlanType = RatePlanType.TIERED;

    private BigDecimal overageUnitRate;
    private Integer graceBuffer;
}
