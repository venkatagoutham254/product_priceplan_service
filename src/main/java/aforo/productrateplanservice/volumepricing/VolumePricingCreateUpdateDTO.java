package aforo.productrateplanservice.volumepricing;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolumePricingCreateUpdateDTO {
    private Integer startRange;
    private Integer endRange;
    private BigDecimal unitPrice;
}
