package aforo.productrateplanservice.stairsteppricing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StairStepPricingRepository extends JpaRepository<StairStepPricing, Long> {
    List<StairStepPricing> findByRatePlanRatePlanId(Long ratePlanId);
}
