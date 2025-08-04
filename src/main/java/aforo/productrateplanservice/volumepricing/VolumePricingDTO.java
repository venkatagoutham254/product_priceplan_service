package aforo.productrateplanservice.volumepricing;

import aforo.productrateplanservice.enums.RatePlanType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolumePricingDTO {

    private Long volumePricingId;

    private Long ratePlanId;

    private Integer startRange;

    private Integer endRange;

    private BigDecimal unitPrice;

    private String volumeBracket;

    private final RatePlanType ratePlanType = RatePlanType.VOLUME_BASED;

    private BigDecimal overageUnitRate;
    private Integer graceBuffer;
}
