package aforo.productrateplanservice.rate_plan;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatePlanDTO {

    private Long ratePlanId;
    private String ratePlanName;
    private String description;
    private RatePlanType ratePlanType;
    private BillingFrequency billingFrequency;
    private Long productId;
    private String productName;
}
