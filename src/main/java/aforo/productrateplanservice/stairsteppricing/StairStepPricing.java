package aforo.productrateplanservice.stairsteppricing;

import aforo.productrateplanservice.enums.RatePlanType;
import aforo.productrateplanservice.rate_plan.RatePlan;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "rate_plan_stair_step_pricing")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StairStepPricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stair_step_pricing_id")
    private Long stairStepPricingId;

    @Column(name = "usage_threshold_start", nullable = false)
    private Integer usageThresholdStart;

    @Column(name = "usage_threshold_end")
    private Integer usageThresholdEnd;

    @Column(name = "monthly_charge", nullable = false)
    private BigDecimal monthlyCharge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_plan_id", nullable = false)
    private RatePlan ratePlan;

    @Column(name = "stair_bracket", nullable = false)
    private String stairBracket;
    @Column(name = "overage_unit_rate")
    private BigDecimal overageUnitRate;
    
    @Column(name = "grace_buffer")
    private Integer graceBuffer;
    
    
}
