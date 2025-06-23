package aforo.productrateplanservice.rate_plan;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRatePlanRequest {

    private String ratePlanName;
    private String productName;
    private String description;
    private RatePlanType ratePlanType;
    private BillingFrequency billingFrequency;
}

