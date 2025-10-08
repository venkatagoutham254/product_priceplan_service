package aforo.productrateplanservice.flatfee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import java.util.Collection;

public interface FlatFeeRepository extends JpaRepository<FlatFee, Long> {
    Optional<FlatFee> findByRatePlanId(Long ratePlanId);
    boolean existsByRatePlanId(Long ratePlanId);
    List<FlatFee> findByRatePlanIdIn(Collection<Long> ratePlanIds);
}
