package aforo.productrateplanservice.product.status;

import aforo.productrateplanservice.client.BillableMetricClient;
import aforo.productrateplanservice.client.SubscriptionServiceClient;
import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.enums.ProductStatus;
import aforo.productrateplanservice.product.enums.RatePlanStatus;
import aforo.productrateplanservice.rate_plan.RatePlanRepository;
import aforo.productrateplanservice.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductStatusResolver {

    private final BillableMetricClient billableMetricClient;
    private final RatePlanRepository ratePlanRepository;
    private final SubscriptionServiceClient subscriptionServiceClient;

    public ProductStatus compute(Product product) {
        if (product == null || product.getProductId() == null) {
            return ProductStatus.DRAFT;
        }

        // Respect persisted lifecycle floor. If not finalized yet (stored DRAFT), do not auto-promote.
        ProductStatus stored = product.getStatus() == null ? ProductStatus.DRAFT : product.getStatus();
        if (stored == ProductStatus.DRAFT) {
            return ProductStatus.DRAFT;
        }

        // From CONFIGURED onwards, derive escalations without downgrading below stored
        Long productId = product.getProductId();

        // CONFIGURED -> MEASURED (if at least one ACTIVE metric exists)
        int activeMetrics = 0;
        try {
            activeMetrics = billableMetricClient.getMetricsByProductId(productId).size();
        } catch (Exception ignored) { /* treat as 0 */ }
        if (activeMetrics == 0) return ProductStatus.CONFIGURED;

        // MEASURED -> PRICED (if at least one ACTIVE rate plan exists)
        Long orgId = TenantContext.require();
        long activePlans = 0;
        try {
            activePlans = ratePlanRepository
                    .countByProduct_ProductIdAndOrganizationIdAndStatus(productId, orgId, RatePlanStatus.ACTIVE);
        } catch (Exception ignored) {
            activePlans = 0;
        }
        if (activePlans == 0) return ProductStatus.MEASURED;

        // PRICED -> LIVE (if at least one ACTIVE subscription exists)
        boolean live = false;
        try {
            live = subscriptionServiceClient.hasActiveSubscriptionForProduct(productId);
        } catch (Exception ignored) { live = false; }
        if (!live) return ProductStatus.PRICED;

        return ProductStatus.LIVE;
    }
}
