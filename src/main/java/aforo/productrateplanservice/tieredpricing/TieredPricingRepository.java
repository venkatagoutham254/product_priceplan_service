package aforo.productrateplanservice.tieredpricing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TieredPricingRepository extends JpaRepository<TieredPricing, Long> {
    List<TieredPricing> findByRatePlan_RatePlanId(Long ratePlanId);
}
