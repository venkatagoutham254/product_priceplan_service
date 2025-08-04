package aforo.productrateplanservice.volumepricing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VolumePricingRepository extends JpaRepository<VolumePricing, Long> {
}
