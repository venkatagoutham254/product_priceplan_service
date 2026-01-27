package aforo.productrateplanservice.rate_plan;

import lombok.*;
import aforo.productrateplanservice.enums.*;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import aforo.productrateplanservice.product.enums.RatePlanStatus;
import aforo.productrateplanservice.rate_plan.RatePlan.PaymentType;
import aforo.productrateplanservice.flatfee.FlatFeeDTO;
import aforo.productrateplanservice.tieredpricing.TieredPricingDTO;
import aforo.productrateplanservice.volumepricing.VolumePricingDTO;
import aforo.productrateplanservice.usagebasedpricing.UsageBasedPricingDTO;
import aforo.productrateplanservice.stairsteppricing.StairStepPricingDTO;
import aforo.productrateplanservice.setupfee.SetupFeeDTO;
import aforo.productrateplanservice.discount.DiscountDTO;
import aforo.productrateplanservice.freemium.FreemiumDTO;
import aforo.productrateplanservice.minimumcommitment.MinimumCommitmentDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatePlanDTO {

    private Long ratePlanId;
    private String ratePlanCode;
    private String ratePlanName;
    private String description;
    private BillingFrequency billingFrequency;
    private Long productId;
    private String productName;
    private String icon;
    private PaymentType paymentType;
    private Long billableMetricId;

    private RatePlanStatus status;
    private LocalDateTime createdOn;
    private LocalDateTime lastUpdated;

    // Embedded pricing configurations (omit entirely when not created)
    @JsonInclude(Include.NON_EMPTY)
    private FlatFeeDTO flatFee;
    @JsonInclude(Include.NON_EMPTY)
    private List<VolumePricingDTO> volumePricings;
    @JsonInclude(Include.NON_EMPTY)
    private List<UsageBasedPricingDTO> usageBasedPricings;
    @JsonInclude(Include.NON_EMPTY)
    private List<TieredPricingDTO> tieredPricings;
    @JsonInclude(Include.NON_EMPTY)
    private List<StairStepPricingDTO> stairStepPricings;

    // Embedded extras (omit entirely when not created)
    @JsonInclude(Include.NON_EMPTY)
    private List<SetupFeeDTO> setupFees;
    @JsonInclude(Include.NON_EMPTY)
    private List<DiscountDTO> discounts;
    @JsonInclude(Include.NON_EMPTY)
    private List<FreemiumDTO> freemiums;
    @JsonInclude(Include.NON_EMPTY)
    private List<MinimumCommitmentDTO> minimumCommitments;

}
