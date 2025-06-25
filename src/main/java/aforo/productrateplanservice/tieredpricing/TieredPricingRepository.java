package aforo.productrateplanservice.tieredpricing;

import org.springframework.data.jpa.repository.JpaRepository;

import aforo.productrateplanservice.volumepricing.VolumePricing;

import java.util.List;
import java.util.Optional;

public interface TieredPricingRepository extends JpaRepository<TieredPricing, Long> {
    List<TieredPricing> findByRatePlan_RatePlanId(Long ratePlanId);
    Optional<TieredPricing> findByIdAndRatePlan_RatePlanId(Long id, Long ratePlanId);
}
