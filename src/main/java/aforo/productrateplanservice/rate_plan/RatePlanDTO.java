package aforo.productrateplanservice.rate_plan;

import lombok.*;
import aforo.productrateplanservice.enums.*;

import java.time.LocalDateTime;

import aforo.productrateplanservice.product.enums.RatePlanStatus;
import aforo.productrateplanservice.rate_plan.RatePlan.PaymentType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatePlanDTO {

    private Long ratePlanId;
    private String ratePlanName;
    private String description;
    private BillingFrequency billingFrequency;
    private Long productId;
    private String productName;
    private PaymentType paymentType;
    private Long billableMetricId;

    private RatePlanStatus status;
    private LocalDateTime createdOn;
    private LocalDateTime lastUpdated;

}
