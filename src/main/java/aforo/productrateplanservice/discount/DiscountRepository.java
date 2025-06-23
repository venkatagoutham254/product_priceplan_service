package aforo.productrateplanservice.discount;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
    List<Discount> findByRatePlan_RatePlanId(Long ratePlanId);
}
