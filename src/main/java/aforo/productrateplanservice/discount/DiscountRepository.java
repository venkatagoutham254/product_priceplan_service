package aforo.productrateplanservice.discount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Collection;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
    @EntityGraph(attributePaths = {"ratePlan"})
    List<Discount> findByRatePlan_RatePlanId(Long ratePlanId);

    @EntityGraph(attributePaths = {"ratePlan"})
    List<Discount> findByRatePlan_RatePlanIdIn(Collection<Long> ratePlanIds);
}
