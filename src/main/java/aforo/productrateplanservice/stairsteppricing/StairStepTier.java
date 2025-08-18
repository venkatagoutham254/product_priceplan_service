package aforo.productrateplanservice.stairsteppricing;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "rate_plan_stair_step_tier")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StairStepTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stair_step_tier_id")
    private Long stairStepTierId;

    @Column(name = "usage_start", nullable = false)
    private Integer usageStart;

    @Column(name = "usage_end")
    private Integer usageEnd; // nullable → means "no upper limit"

    @Column(name = "flat_cost", nullable = false)
    private BigDecimal flatCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stair_step_pricing_id", nullable = false)
    private StairStepPricing stairStepPricing;
}
