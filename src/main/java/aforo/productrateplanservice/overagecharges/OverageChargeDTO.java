package aforo.productrateplanservice.overagecharges;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OverageChargeDTO {
    private Long id;
    private Long ratePlanId;
    private Integer overageUnitRate;
    private Integer graceBuffer;
}
