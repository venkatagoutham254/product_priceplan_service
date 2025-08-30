package aforo.productrateplanservice.rate_plan;


import jakarta.validation.constraints.NotNull;
import aforo.productrateplanservice.rate_plan.RatePlan.PaymentType;
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
    private BillingFrequency billingFrequency;
    private PaymentType paymentType;
    
    private Long billableMetricId;
}

