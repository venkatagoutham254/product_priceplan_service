package aforo.productrateplanservice.volumepricing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository

public interface VolumePricingRepository extends JpaRepository<VolumePricing, Long> {
    List<VolumePricing> findByRatePlan_RatePlanId(Long ratePlanId);
    void deleteByRatePlan_RatePlanId(Long ratePlanId);

    Optional<VolumePricing> findByIdAndRatePlan_RatePlanId(Long id, Long ratePlanId);

}
