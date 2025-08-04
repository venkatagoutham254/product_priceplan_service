package aforo.productrateplanservice.volumepricing;

import aforo.productrateplanservice.enums.RatePlanType;
import aforo.productrateplanservice.rate_plan.RatePlan;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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

    @Column(name = "start_range", nullable = false)
    private Integer startRange;

    @Column(name = "end_range")
    private Integer endRange;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_plan_id", nullable = false)
    private RatePlan ratePlan;

    @Column(name = "volume_bracket", nullable = false)
    private String volumeBracket;

    @Enumerated(EnumType.STRING)
    @Column(name = "rate_plan_type", nullable = false, updatable = false)
    private final RatePlanType ratePlanType = RatePlanType.VOLUME_BASED;

    @Column(name = "overage_unit_rate")
    private BigDecimal overageUnitRate;
    
    @Column(name = "grace_buffer")
    private Integer graceBuffer;
    
}
