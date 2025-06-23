package aforo.productrateplanservice.stairsteppricing;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StairStepPricingCreateUpdateDTO {
    private Integer usageThresholdStart;
    private Integer usageThresholdEnd;
    private BigDecimal monthlyCharge;
}
