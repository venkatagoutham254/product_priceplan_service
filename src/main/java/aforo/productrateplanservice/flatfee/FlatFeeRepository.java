package aforo.productrateplanservice.flatfee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FlatFeeRepository extends JpaRepository<FlatFee, Long> {
    Optional<FlatFee> findByRatePlanId(Long ratePlanId);
    boolean existsByRatePlanId(Long ratePlanId);
}
