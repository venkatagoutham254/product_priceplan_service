package aforo.productrateplanservice.flatfee;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlatFeeCreateUpdateDTO {

    @Min(value = 0, message = "flatFeeAmount must be non-negative")
    private Integer flatFeeAmount;

    @Min(value = 0, message = "numberOfApiCalls must be non-negative")
    private Integer numberOfApiCalls;

    private BigDecimal overageUnitRate;
    private Integer graceBuffer;
    
}
