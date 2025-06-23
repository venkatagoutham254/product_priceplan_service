package aforo.productrateplanservice.flatfee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FlatFeeRepository extends JpaRepository<FlatFee, Long> {
    Optional<FlatFee> findByRatePlan_RatePlanId(Long ratePlanId);
    boolean existsByRatePlan_RatePlanId(Long ratePlanId);
}
