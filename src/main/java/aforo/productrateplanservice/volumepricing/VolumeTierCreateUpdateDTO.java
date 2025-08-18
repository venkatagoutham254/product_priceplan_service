package aforo.productrateplanservice.volumepricing;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolumeTierCreateUpdateDTO {

    @NotNull(message = "usageStart is required")
    private Integer usageStart;

    private Integer usageEnd; // optional

    @NotNull(message = "unitPrice is required")
    private BigDecimal unitPrice;

    // Optionally, add custom validation for usageStart < usageEnd if needed
}
