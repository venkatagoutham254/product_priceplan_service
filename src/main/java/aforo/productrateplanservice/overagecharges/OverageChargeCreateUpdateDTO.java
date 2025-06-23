package aforo.productrateplanservice.overagecharges;

import lombok.Data;

@Data
public class OverageChargeCreateUpdateDTO {
    private Integer overageUnitRate;
    private Integer graceBuffer;
}
