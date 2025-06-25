package aforo.productrateplanservice.setupfee;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetupFeeDTO {
    private Long id;
    private Long ratePlanId;
    private BigDecimal setupFee;
    private Integer applicationTiming;
    private String invoiceDescription;
}
