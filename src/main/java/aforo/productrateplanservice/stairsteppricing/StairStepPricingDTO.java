package aforo.productrateplanservice.stairsteppricing;

import aforo.productrateplanservice.enums.RatePlanType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StairStepPricingDTO {

    private Long stairStepPricingId;


    private Integer usageThresholdStart;

    private Integer usageThresholdEnd;

    private BigDecimal monthlyCharge;

    private String stairBracket;

    private final RatePlanType ratePlanType = RatePlanType.STAIRSTEP;

    private BigDecimal overageUnitRate;
    private Integer graceBuffer;
    
}
