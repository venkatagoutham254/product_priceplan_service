package aforo.productrateplanservice.setupfee;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetupFeeCreateUpdateDTO {
    private BigDecimal setupFee;
    private Integer applicationTiming;
    private String invoiceDescription;
}
