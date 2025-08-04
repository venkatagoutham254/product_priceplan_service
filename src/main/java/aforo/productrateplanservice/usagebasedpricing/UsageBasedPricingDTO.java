package aforo.productrateplanservice.usagebasedpricing;

import aforo.productrateplanservice.enums.RatePlanType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageBasedPricingDTO {

    private Long usageBasedPricingId;
    private Long ratePlanId;
    private BigDecimal perUnitAmount;
    private final RatePlanType ratePlanType = RatePlanType.USAGE_BASED;
}
