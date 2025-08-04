package aforo.productrateplanservice.flatfee;

import java.math.BigDecimal;

import aforo.productrateplanservice.enums.RatePlanType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlatFeeDTO {

    private Long flatFeeId;
    private Long ratePlanId;

    private Integer flatFeeAmount;
    private Integer numberOfApiCalls;

    private RatePlanType ratePlanType;

    private BigDecimal overageUnitRate;
    private Integer graceBuffer;
    
}
