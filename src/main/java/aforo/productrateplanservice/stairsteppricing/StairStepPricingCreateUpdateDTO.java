package aforo.productrateplanservice.stairsteppricing;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StairStepPricingCreateUpdateDTO {

    @NotNull
    private Integer usageThresholdStart;

    private Integer usageThresholdEnd;

    @NotNull
    private BigDecimal monthlyCharge;

    @NotNull
    private String stairBracket;

    private BigDecimal overageUnitRate;
    private Integer graceBuffer;
    
}
