package aforo.productrateplanservice.overagecharges;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OverageChargeRepository extends JpaRepository<OverageCharge, Long> {
    List<OverageCharge> findByRatePlan_RatePlanId(Long ratePlanId);
}
