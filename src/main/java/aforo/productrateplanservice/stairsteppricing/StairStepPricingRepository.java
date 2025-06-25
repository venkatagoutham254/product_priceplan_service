package aforo.productrateplanservice.stairsteppricing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StairStepPricingRepository extends JpaRepository<StairStepPricing, Long> {
    List<StairStepPricing> findByRatePlanRatePlanId(Long ratePlanId);
    Optional<StairStepPricing> findByIdAndRatePlan_RatePlanId(Long id, Long ratePlanId);
}
