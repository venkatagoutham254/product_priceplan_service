package aforo.productrateplanservice.stairsteppricing;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StairStepPricingDTO {
    private Long id;
    private Integer usageThresholdStart;
    private Integer usageThresholdEnd;
    private BigDecimal monthlyCharge;
    private String stairBracket;
}
