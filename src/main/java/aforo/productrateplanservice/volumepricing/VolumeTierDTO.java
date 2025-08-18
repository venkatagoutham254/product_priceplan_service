package aforo.productrateplanservice.volumepricing;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolumeTierDTO {

    private Long volumeTierId;

    private Integer usageStart;

    private Integer usageEnd;

    private BigDecimal unitPrice;
}
