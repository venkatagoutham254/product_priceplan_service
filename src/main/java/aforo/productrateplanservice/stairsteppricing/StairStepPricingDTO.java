package aforo.productrateplanservice.stairsteppricing;

import aforo.productrateplanservice.enums.RatePlanType;
import lombok.*;
import java.util.List;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StairStepPricingDTO {

    private Long stairStepPricingId;


    private List<StairStepTierDTO> tiers;

    private final RatePlanType ratePlanType = RatePlanType.STAIRSTEP;

    private BigDecimal overageUnitRate;
    private Integer graceBuffer;
    
}
