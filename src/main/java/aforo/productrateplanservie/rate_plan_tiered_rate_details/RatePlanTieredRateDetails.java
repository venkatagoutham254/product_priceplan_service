package aforo.productrateplanservie.rate_plan_tiered_rate_details;

import aforo.productrateplanservie.rate_plan_tiered_rate.RatePlanTieredRate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class RatePlanTieredRateDetails {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tierNumber;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal tierStart;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal tierRate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal tierEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_plan_tiered_rate_id", nullable = false)
    private RatePlanTieredRate ratePlanTieredRate;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

	public Object getTierNumber() {
		// TODO Auto-generated method stub
		return null;
	}

}
