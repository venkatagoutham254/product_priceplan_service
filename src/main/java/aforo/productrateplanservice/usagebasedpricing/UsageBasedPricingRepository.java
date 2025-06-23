package aforo.productrateplanservice.usagebasedpricing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsageBasedPricingRepository extends JpaRepository<UsageBasedPricing, Long> {
    Optional<UsageBasedPricing> findByRatePlanRatePlanId(Long ratePlanId);
}
