package aforo.productrateplanservice.stairsteppricing;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StairStepTierDTO {

    private Long stairStepTierId;

    private Integer usageStart;

    private Integer usageEnd;

    private BigDecimal flatCost;
}
