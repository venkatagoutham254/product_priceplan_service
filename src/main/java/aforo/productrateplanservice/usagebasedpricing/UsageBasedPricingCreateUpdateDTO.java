package aforo.productrateplanservice.usagebasedpricing;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageBasedPricingCreateUpdateDTO {

    @NotNull(message = "perUnitAmount is required")
    private BigDecimal perUnitAmount;
}
