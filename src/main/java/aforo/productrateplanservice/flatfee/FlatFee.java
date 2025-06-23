package aforo.productrateplanservice.flatfee;

import aforo.productrateplanservice.rate_plan.RatePlan;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rate_plan_flat_fee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlatFee {

    @Id
    @Column(name = "rate_plan_id")
    private Long ratePlanId;

    @Column(name = "flat_fee_amount", nullable = false)
    private Integer flatFeeAmount;

    @Column(name = "usage_limit", nullable = false)
    private Integer usageLimit;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "rate_plan_id")
    private RatePlan ratePlan;
}
