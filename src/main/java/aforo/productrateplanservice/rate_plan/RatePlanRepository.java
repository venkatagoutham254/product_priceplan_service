package aforo.productrateplanservice.rate_plan;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface RatePlanRepository extends JpaRepository<RatePlan, Long> {

    Optional<RatePlan> findByRatePlanNameAndProduct_ProductId(String ratePlanName, Long productId);
    Optional<RatePlan> findByRatePlanNameAndProduct_ProductIdAndOrganizationId(String ratePlanName, Long productId, Long organizationId);

    List<RatePlan> findByProduct_ProductId(Long productId);
    List<RatePlan> findByProduct_ProductIdAndOrganizationId(Long productId, Long organizationId);

    void deleteByProduct_ProductId(Long productId);
    void deleteByProduct_ProductIdAndOrganizationId(Long productId, Long organizationId);

    List<RatePlan> findAllByOrganizationId(Long organizationId);
    Optional<RatePlan> findByRatePlanIdAndOrganizationId(Long ratePlanId, Long organizationId);

}
