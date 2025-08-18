package aforo.productrateplanservice.volumepricing;

import aforo.productrateplanservice.enums.RatePlanType;
import aforo.productrateplanservice.rate_plan.RatePlan;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "rate_plan_volume_pricing")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolumePricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long volumePricingId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_plan_id", nullable = false)
    private RatePlan ratePlan;

    @OneToMany(mappedBy = "volumePricing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VolumeTier> tiers;


    @Column(name = "overage_unit_rate")
    private BigDecimal overageUnitRate;
    
    @Column(name = "grace_buffer")
    private Integer graceBuffer;
    
}
