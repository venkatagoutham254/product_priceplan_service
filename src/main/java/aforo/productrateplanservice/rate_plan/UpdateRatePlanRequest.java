package aforo.productrateplanservice.rate_plan;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import aforo.productrateplanservice.rate_plan.RatePlan.PaymentType;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateRatePlanRequest {

    private String ratePlanName;
    private String description;
    private BillingFrequency billingFrequency;
    private PaymentType paymentType;
    @NotNull(message = "billableMetricId must not be null")
    private Long billableMetricId;
}
