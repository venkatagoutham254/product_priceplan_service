package aforo.productrateplanservice.setupfee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;
import java.util.Collection;

public interface SetupFeeRepository extends JpaRepository<SetupFee, Long> {
    @EntityGraph(attributePaths = {"ratePlan"})
    Optional<SetupFee> findByRatePlanRatePlanId(Long ratePlanId);
    @EntityGraph(attributePaths = {"ratePlan"})
    List<SetupFee> findByRatePlan_RatePlanId(Long ratePlanId);
    @EntityGraph(attributePaths = {"ratePlan"})
    List<SetupFee> findByRatePlan_RatePlanIdIn(Collection<Long> ratePlanIds);
}
