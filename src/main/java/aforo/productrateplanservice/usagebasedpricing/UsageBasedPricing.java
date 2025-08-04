package aforo.productrateplanservice.usagebasedpricing;

import aforo.productrateplanservice.rate_plan.RatePlan;
import aforo.productrateplanservice.enums.RatePlanType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "rate_plan_usage_based_pricing")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageBasedPricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_based_pricing_id")
    private Long usageBasedPricingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_plan_id", nullable = false)
    private RatePlan ratePlan;

    @Column(name = "per_unit_amount", nullable = false)
    private BigDecimal perUnitAmount;

    @Column(name = "rate_plan_type", nullable = false, updatable = false)
    private final RatePlanType ratePlanType = RatePlanType.USAGE_BASED;
}
