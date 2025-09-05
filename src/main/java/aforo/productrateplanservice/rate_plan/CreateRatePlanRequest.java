package aforo.productrateplanservice.rate_plan;

import aforo.productrateplanservice.rate_plan.RatePlan.PaymentType;
import lombok.*;
    
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRatePlanRequest {

    private String ratePlanName;
    private Long productId;
    private String description;
    private BillingFrequency billingFrequency;
    private PaymentType paymentType;
    
    private Long billableMetricId;
}

