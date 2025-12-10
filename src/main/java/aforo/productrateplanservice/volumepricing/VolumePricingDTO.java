package aforo.productrateplanservice.volumepricing;

import aforo.productrateplanservice.enums.RatePlanType;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolumePricingDTO {

    private Long volumePricingId;

    private Long ratePlanId;

    private List<VolumeTierDTO> tiers;

    private final RatePlanType ratePlanType = RatePlanType.VOLUME_BASED;

    private BigDecimal overageUnitRate;
    private Integer graceBuffer;
}
