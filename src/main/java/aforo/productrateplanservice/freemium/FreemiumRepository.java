package aforo.productrateplanservice.freemium;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Collection;

public interface FreemiumRepository extends JpaRepository<Freemium, Long> {
    @EntityGraph(attributePaths = {"ratePlan"})
    List<Freemium> findByRatePlan_RatePlanId(Long ratePlanId);
    @EntityGraph(attributePaths = {"ratePlan"})
    List<Freemium> findByRatePlan_RatePlanIdIn(Collection<Long> ratePlanIds);
}
