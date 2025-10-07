package aforo.productrateplanservice.tieredpricing;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TieredPricingRepository extends JpaRepository<TieredPricing, Long> {
    @EntityGraph(attributePaths = {"tiers", "ratePlan"})
    List<TieredPricing> findByRatePlan_RatePlanId(Long ratePlanId);
}
