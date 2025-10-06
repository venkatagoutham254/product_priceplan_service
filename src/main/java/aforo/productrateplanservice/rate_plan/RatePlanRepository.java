package aforo.productrateplanservice.rate_plan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

import aforo.productrateplanservice.product.enums.RatePlanStatus;

public interface RatePlanRepository extends JpaRepository<RatePlan, Long> {

    Optional<RatePlan> findByRatePlanNameAndProduct_ProductId(String ratePlanName, Long productId);
    Optional<RatePlan> findByRatePlanNameAndProduct_ProductIdAndOrganizationId(String ratePlanName, Long productId, Long organizationId);

    List<RatePlan> findByProduct_ProductId(Long productId);
    @EntityGraph(attributePaths = "product")
    List<RatePlan> findByProduct_ProductIdAndOrganizationId(Long productId, Long organizationId);

    void deleteByProduct_ProductId(Long productId);
    void deleteByProduct_ProductIdAndOrganizationId(Long productId, Long organizationId);

    @EntityGraph(attributePaths = "product")
    List<RatePlan> findAllByOrganizationId(Long organizationId);
    @EntityGraph(attributePaths = "product")
    Optional<RatePlan> findByRatePlanIdAndOrganizationId(Long ratePlanId, Long organizationId);

    // Delete by billable metric id for a given tenant
    void deleteByBillableMetricIdAndOrganizationId(Long billableMetricId, Long organizationId);

    // Efficient count for ACTIVE rate plans by product and org
    long countByProduct_ProductIdAndOrganizationIdAndStatus(Long productId, Long organizationId, RatePlanStatus status);

}

