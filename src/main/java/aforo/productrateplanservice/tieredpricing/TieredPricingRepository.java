package aforo.productrateplanservice.tieredpricing;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Collection;

public interface TieredPricingRepository extends JpaRepository<TieredPricing, Long> {
    @EntityGraph(attributePaths = {"tiers", "ratePlan"})
    List<TieredPricing> findByRatePlan_RatePlanId(Long ratePlanId);

    @EntityGraph(attributePaths = {"tiers", "ratePlan"})
    List<TieredPricing> findByRatePlan_RatePlanIdIn(Collection<Long> ratePlanIds);

    // Eagerly fetch associations for 'all' and 'by id'
    @Override
    @EntityGraph(attributePaths = {"tiers", "ratePlan"})
    List<TieredPricing> findAll();

    @EntityGraph(attributePaths = {"tiers", "ratePlan"})
    java.util.Optional<TieredPricing> findByTieredPricingId(Long id);
}
