package aforo.productrateplanservice.volumepricing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VolumePricingRepository extends JpaRepository<VolumePricing, Long> {
    List<VolumePricing> findByRatePlanRatePlanId(Long ratePlanId);
    Optional<VolumePricing> findById(Long id);
}
