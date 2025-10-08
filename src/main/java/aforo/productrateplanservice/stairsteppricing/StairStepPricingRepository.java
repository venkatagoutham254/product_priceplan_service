package aforo.productrateplanservice.stairsteppricing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Collection;

public interface StairStepPricingRepository extends JpaRepository<StairStepPricing, Long> {
    @EntityGraph(attributePaths = {"tiers", "ratePlan"})
    List<StairStepPricing> findByRatePlanRatePlanId(Long ratePlanId);
    @EntityGraph(attributePaths = {"tiers", "ratePlan"})
    List<StairStepPricing> findByRatePlan_RatePlanIdIn(Collection<Long> ratePlanIds);

    // Eagerly fetch associations for 'all' and 'by id'
    @Override
    @EntityGraph(attributePaths = {"tiers", "ratePlan"})
    List<StairStepPricing> findAll();

    @EntityGraph(attributePaths = {"tiers", "ratePlan"})
    java.util.Optional<StairStepPricing> findByStairStepPricingId(Long id);
}
