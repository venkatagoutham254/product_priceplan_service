package aforo.productrateplanservice.volumepricing;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

import aforo.productrateplanservice.rate_plan.RatePlan;

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
    private Long id;

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

}
