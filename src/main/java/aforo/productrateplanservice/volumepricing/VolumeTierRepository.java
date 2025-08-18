package aforo.productrateplanservice.volumepricing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VolumeTierRepository extends JpaRepository<VolumeTier, Long> {
    List<VolumeTier> findByVolumePricingVolumePricingId(Long volumePricingId);
}
