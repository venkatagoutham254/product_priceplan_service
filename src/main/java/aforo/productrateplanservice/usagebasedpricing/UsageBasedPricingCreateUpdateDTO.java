package aforo.productrateplanservice.usagebasedpricing;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageBasedPricingCreateUpdateDTO {
    private BigDecimal perUnitAmount;
}
