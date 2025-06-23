package aforo.productrateplanservice.rate_plan;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateRatePlanRequest {

    private String ratePlanName;
    private String description;
    private RatePlanType ratePlanType;
    private BillingFrequency billingFrequency;
}
