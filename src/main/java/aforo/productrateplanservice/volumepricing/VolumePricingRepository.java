package aforo.productrateplanservice.volumepricing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;
import java.util.Collection;

@Repository
public interface VolumePricingRepository extends JpaRepository<VolumePricing, Long> {
    @EntityGraph(attributePaths = {"tiers", "ratePlan"})
    List<VolumePricing> findByRatePlanRatePlanId(Long ratePlanId);
    Optional<VolumePricing> findById(Long id);
    @EntityGraph(attributePaths = {"tiers", "ratePlan"})
    List<VolumePricing> findByRatePlan_RatePlanIdIn(Collection<Long> ratePlanIds);

    // Eagerly fetch associations for 'all' and 'by id'
    @Override
    @EntityGraph(attributePaths = {"tiers", "ratePlan"})
    List<VolumePricing> findAll();

    @EntityGraph(attributePaths = {"tiers", "ratePlan"})
    Optional<VolumePricing> findByVolumePricingId(Long id);
}
