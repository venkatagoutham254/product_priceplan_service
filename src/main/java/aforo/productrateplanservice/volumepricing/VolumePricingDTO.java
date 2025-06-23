package aforo.productrateplanservice.volumepricing;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolumePricingDTO {
    private Long id;
    private Integer startRange;
    private Integer endRange;
    private BigDecimal unitPrice;
}

