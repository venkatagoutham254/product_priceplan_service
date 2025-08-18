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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_plan_id", nullable = false)
    private RatePlan ratePlan;

    @Column(name = "overage_unit_rate")
    private java.math.BigDecimal overageUnitRate;

    @Column(name = "grace_buffer")
    private Integer graceBuffer;

    @Column(name = "rate_plan_type", nullable = false, updatable = false)
    private final RatePlanType ratePlanType = RatePlanType.TIERED;

    @OneToMany(mappedBy = "tieredPricing", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<TieredTier> tiers = new java.util.ArrayList<>();
}
