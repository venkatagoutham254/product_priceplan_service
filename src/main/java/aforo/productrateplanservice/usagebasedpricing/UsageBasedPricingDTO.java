package aforo.productrateplanservice.usagebasedpricing;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageBasedPricingDTO {
    private Long id;
    private Long ratePlanId;
    private BigDecimal perUnitAmount;
}
