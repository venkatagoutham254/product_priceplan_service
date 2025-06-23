package aforo.productrateplanservice.rate_plan;

import aforo.productrateplanservice.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "aforo_rate_plan", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"rate_plan_name", "product_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ratePlanId;

    @Column(name = "rate_plan_name", nullable = false)
    private String ratePlanName;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "rate_plan_type", nullable = false)
    private RatePlanType ratePlanType;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_frequency", nullable = false)
    private BillingFrequency billingFrequency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
