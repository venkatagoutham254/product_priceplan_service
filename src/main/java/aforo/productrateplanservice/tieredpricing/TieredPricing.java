package aforo.productrateplanservice.tieredpricing;

import aforo.productrateplanservice.rate_plan.RatePlan;
import aforo.productrateplanservice.enums.RatePlanType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "rate_plan_tiered_pricing")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TieredPricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tiered_pricing_id")
    private Long tieredPricingId;

    @Column(name = "start_range", nullable = false)
    private Integer startRange;

    @Column(name = "end_range")
    private Integer endRange;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_plan_id", nullable = false)
    private RatePlan ratePlan;

    @Column(name = "tier_bracket", nullable = false)
    private String tierBracket;

    @Column(name = "rate_plan_type", nullable = false, updatable = false)
    private final RatePlanType ratePlanType = RatePlanType.TIERED;


    @Column(name = "overage_unit_rate")
    private BigDecimal overageUnitRate;
    
    @Column(name = "grace_buffer")
    private Integer graceBuffer;
}
