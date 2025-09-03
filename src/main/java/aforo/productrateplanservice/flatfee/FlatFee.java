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
@Table(name = "rate_plan_flat_fee")
public class FlatFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flat_fee_id")
    private Long flatFeeId;

    @NotNull(message = "ratePlanId is required")
    @Column(name = "rate_plan_id", nullable = false, unique = true)
    private Long ratePlanId;

    @Min(value = 0, message = "flatFeeAmount must be non-negative")
    @Column(name = "flat_fee_amount", nullable = true)
    private Integer flatFeeAmount;

    @Min(value = 0, message = "numberOfApiCalls must be non-negative")
    @Column(name = "number_of_api_calls", nullable = true)
    private Integer numberOfApiCalls;


    @Enumerated(EnumType.STRING)
    @Transient
    private final RatePlanType ratePlanType = RatePlanType.FLATFEE;

    @Column(name = "overage_unit_rate", nullable = true)
    private BigDecimal overageUnitRate;

    @Column(name = "grace_buffer", nullable = true)
    private Integer graceBuffer;
    
}
