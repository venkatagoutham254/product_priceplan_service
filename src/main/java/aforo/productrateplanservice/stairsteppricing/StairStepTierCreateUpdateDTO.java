package aforo.productrateplanservice.stairsteppricing;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StairStepTierCreateUpdateDTO {

    @NotNull
    private Integer usageStart;

    private Integer usageEnd; // optional

    @NotNull
    private BigDecimal flatCost;
}
