package aforo.productrateplanservice.volumepricing;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolumePricingCreateUpdateDTO {

    @NotNull
    @Min(0)
    private Integer startRange;

    @Min(0)
    private Integer endRange;

    @NotNull
    private BigDecimal unitPrice;

    @NotNull
    private String volumeBracket;

    private BigDecimal overageUnitRate;
    private Integer graceBuffer;
}
