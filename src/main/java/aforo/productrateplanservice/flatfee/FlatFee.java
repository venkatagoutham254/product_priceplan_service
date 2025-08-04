package aforo.productrateplanservice.flatfee;

import java.math.BigDecimal;

import aforo.productrateplanservice.enums.RatePlanType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "flat_fee")
public class FlatFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flat_fee_id")
    private Long flatFeeId;

    @NotNull(message = "ratePlanId is required")
    @Column(name = "rate_plan_id", nullable = false, unique = true)
    private Long ratePlanId;

    @NotNull(message = "flatFeeAmount is required")
    @Min(value = 0, message = "flatFeeAmount must be non-negative")
    @Column(name = "flat_fee_amount", nullable = false)
    private Integer flatFeeAmount;

    @NotNull(message = "numberOfApiCalls is required")
    @Min(value = 0, message = "numberOfApiCalls must be non-negative")
    @Column(name = "number_of_api_calls", nullable = false)
    private Integer numberOfApiCalls;


    @Enumerated(EnumType.STRING)
    @Column(name = "rate_plan_type", nullable = false, updatable = false)
    private final RatePlanType ratePlanType = RatePlanType.FLATFEE;

    @Column(name = "overage_unit_rate",nullable = false)
    private BigDecimal overageUnitRate;

    @Column(name = "grace_buffer")
    private Integer graceBuffer;
    
}
