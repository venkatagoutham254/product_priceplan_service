package aforo.productrateplanservice.flatfee;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlatFeeCreateUpdateDTO {

    @NotNull(message = "flatFeeAmount is required")
    @Min(value = 0, message = "flatFeeAmount must be non-negative")
    private Integer flatFeeAmount;

    @NotNull(message = "numberOfApiCalls is required")
    @Min(value = 0, message = "numberOfApiCalls must be non-negative")
    private Integer numberOfApiCalls;

    @NotNull(message = "overageUnitRate is required")
    private BigDecimal overageUnitRate;
    private Integer graceBuffer;
    
}
