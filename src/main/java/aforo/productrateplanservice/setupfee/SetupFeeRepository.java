package aforo.productrateplanservice.setupfee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SetupFeeRepository extends JpaRepository<SetupFee, Long> {
    Optional<SetupFee> findByRatePlanRatePlanId(Long ratePlanId);
    List<SetupFee> findByRatePlan_RatePlanId(Long ratePlanId);
}
