package aforo.productrateplanservice.minimumcommitment;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MinimumCommitmentCreateUpdateDTO {

    @NotNull
    private Integer minimumUsage;

    @NotNull
    private Integer minimumCharge;
}
