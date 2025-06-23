package aforo.productrateplanservice.setupfee;

import aforo.productrateplanservice.rate_plan.RatePlan;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "rate_plan_setup_fee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SetupFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_plan_id", nullable = false)
    private RatePlan ratePlan;

    @Column(name = "setup_fee", nullable = false)
    private BigDecimal setupFee;

    @Column(name = "application_timing")
    private String applicationTiming;

    @Column(name = "invoice_description")
    private String invoiceDescription;
}
