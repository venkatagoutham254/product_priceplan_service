package aforo.productrateplanservice.volumepricing;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidVolumeTiers
public class VolumePricingCreateUpdateDTO {

    @NotNull(message = "tiers list is required")
    private List<VolumeTierCreateUpdateDTO> tiers;

    private BigDecimal overageUnitRate;
    private Integer graceBuffer;

    // Optionally, add class-level validation for tier overlaps, ordering, etc.
}
