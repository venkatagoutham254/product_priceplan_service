package aforo.productrateplanservice.stairsteppricing;

import aforo.productrateplanservice.enums.RatePlanType;
import aforo.productrateplanservice.rate_plan.RatePlan;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
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

    @OneToMany(mappedBy = "stairStepPricing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StairStepTier> tiers;
    

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_plan_id", nullable = false)
    private RatePlan ratePlan;


    @Column(name = "overage_unit_rate")
    private BigDecimal overageUnitRate;
    
    @Column(name = "grace_buffer")
    private Integer graceBuffer;
    
    
}
