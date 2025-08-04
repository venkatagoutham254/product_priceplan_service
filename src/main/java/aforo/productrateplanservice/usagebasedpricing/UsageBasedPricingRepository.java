package aforo.productrateplanservice.usagebasedpricing;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UsageBasedPricingRepository extends JpaRepository<UsageBasedPricing, Long> {
    List<UsageBasedPricing> findByRatePlanRatePlanId(Long ratePlanId);
}
