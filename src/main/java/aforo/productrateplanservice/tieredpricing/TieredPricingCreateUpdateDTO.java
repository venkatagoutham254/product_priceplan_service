package aforo.productrateplanservice.tieredpricing;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TieredPricingCreateUpdateDTO {

    @NotNull
    private Integer startRange;

    private Integer endRange;

    @NotNull
    private BigDecimal unitPrice;

    @NotNull
    private String tierBracket;

    private BigDecimal overageUnitRate;
    private Integer graceBuffer;
}
