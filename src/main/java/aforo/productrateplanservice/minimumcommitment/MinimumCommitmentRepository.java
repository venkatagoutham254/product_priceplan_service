package aforo.productrateplanservice.minimumcommitment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Collection;

public interface MinimumCommitmentRepository extends JpaRepository<MinimumCommitment, Long> {
    @EntityGraph(attributePaths = {"ratePlan"})
    List<MinimumCommitment> findByRatePlan_RatePlanId(Long ratePlanId);

    @EntityGraph(attributePaths = {"ratePlan"})
    List<MinimumCommitment> findByRatePlan_RatePlanIdIn(Collection<Long> ratePlanIds);
}
