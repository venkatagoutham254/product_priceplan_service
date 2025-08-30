package aforo.productrateplanservice.rate_plan;

import aforo.productrateplanservice.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;
import aforo.productrateplanservice.product.enums.RatePlanStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;




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

    public enum PaymentType {
        POSTPAID,
        PREPAID
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ratePlanId;

    @Column(name = "rate_plan_name")
    private String ratePlanName;

    @Column(name = "description")
    private String description;


    @Enumerated(EnumType.STRING)
    @Column(name = "billing_frequency")
    private BillingFrequency billingFrequency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;



    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType;


    @Column(name = "billable_metric_id")
    private Long billableMetricId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RatePlanStatus status = RatePlanStatus.DRAFT;
    
    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdOn;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime lastUpdated;
    
}
