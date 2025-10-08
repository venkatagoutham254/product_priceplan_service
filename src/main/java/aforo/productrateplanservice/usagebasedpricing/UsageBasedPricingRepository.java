package aforo.productrateplanservice.usagebasedpricing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.Collection;

public interface UsageBasedPricingRepository extends JpaRepository<UsageBasedPricing, Long> {
    @EntityGraph(attributePaths = {"ratePlan"})
    List<UsageBasedPricing> findByRatePlanRatePlanId(Long ratePlanId);
    @EntityGraph(attributePaths = {"ratePlan"})
    List<UsageBasedPricing> findByRatePlan_RatePlanIdIn(Collection<Long> ratePlanIds);
}
