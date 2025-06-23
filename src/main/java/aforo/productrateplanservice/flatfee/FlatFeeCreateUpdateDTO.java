package aforo.productrateplanservice.flatfee;

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

    @NotNull(message = "usageLimit is required")
    @Min(value = 0, message = "usageLimit must be non-negative")
    private Integer usageLimit;
}
