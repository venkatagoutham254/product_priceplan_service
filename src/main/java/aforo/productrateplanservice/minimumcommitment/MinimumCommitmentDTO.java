package aforo.productrateplanservice.minimumcommitment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MinimumCommitmentDTO {
    private Long id;
    private Long ratePlanId;
    private Integer minimumUsage;
    private Integer minimumCharge;
}
