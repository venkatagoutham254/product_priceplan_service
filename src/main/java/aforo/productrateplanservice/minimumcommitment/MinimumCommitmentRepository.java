package aforo.productrateplanservice.minimumcommitment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MinimumCommitmentRepository extends JpaRepository<MinimumCommitment, Long> {
    List<MinimumCommitment> findByRatePlan_RatePlanId(Long ratePlanId);
}
