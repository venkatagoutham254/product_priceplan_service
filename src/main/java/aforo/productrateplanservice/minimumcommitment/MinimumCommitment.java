package aforo.productrateplanservice.minimumcommitment;

import aforo.productrateplanservice.rate_plan.RatePlan;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rate_plan_minimum_commitment")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MinimumCommitment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "rate_plan_id", nullable = false)
    private RatePlan ratePlan;

    @Column(nullable = false)
    private Integer minimumUsage;

    @Column(nullable = false)
    private Integer minimumCharge;
}
