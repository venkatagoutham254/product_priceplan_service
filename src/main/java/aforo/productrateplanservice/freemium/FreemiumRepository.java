package aforo.productrateplanservice.freemium;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FreemiumRepository extends JpaRepository<Freemium, Long> {
    List<Freemium> findByRatePlan_RatePlanId(Long ratePlanId);
}
