package aforo.productrateplanservice.tieredpricing;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TieredPricingCreateUpdateDTO {
    private Integer startRange;
    private Integer endRange;
    private BigDecimal unitPrice;
}
