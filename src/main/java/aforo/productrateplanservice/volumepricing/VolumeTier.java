package aforo.productrateplanservice.volumepricing;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "rate_plan_volume_tier")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolumeTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "volume_tier_id")
    private Long volumeTierId;

    @Column(name = "usage_start", nullable = false)
    private Integer usageStart;

    @Column(name = "usage_end")
    private Integer usageEnd; // nullable → unlimited

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volume_pricing_id", nullable = false)
    private VolumePricing volumePricing;
}
