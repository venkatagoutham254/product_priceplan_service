package aforo.productrateplanservice.tieredpricing;

import aforo.productrateplanservice.rate_plan.RatePlan;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "rate_plan_tiered_tier")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TieredTier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tiered_tier_id")
    private Long tieredTierId;

    @Column(name = "start_range", nullable = false)
    private Integer startRange;

    @Column(name = "end_range")
    private Integer endRange;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tiered_pricing_id", nullable = false)
    private TieredPricing tieredPricing;
}
