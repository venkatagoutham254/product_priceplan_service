package aforo.productrateplanservice.flatfee;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlatFeeDTO {
    private Long ratePlanId;
    private Integer flatFeeAmount;
    private Integer usageLimit;
}

