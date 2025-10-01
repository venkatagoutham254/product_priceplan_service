package aforo.productrateplanservice.product.status;

import aforo.productrateplanservice.client.BillableMetricClient;
import aforo.productrateplanservice.client.SubscriptionServiceClient;
import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.entity.ProductAPI;
import aforo.productrateplanservice.product.entity.ProductFlatFile;
import aforo.productrateplanservice.product.entity.ProductLLMToken;
import aforo.productrateplanservice.product.entity.ProductSQLResult;
import aforo.productrateplanservice.product.entity.ProductStorage;
import aforo.productrateplanservice.product.enums.ProductStatus;
import aforo.productrateplanservice.product.enums.RatePlanStatus;
import aforo.productrateplanservice.product.repository.ProductAPIRepository;
import aforo.productrateplanservice.product.repository.ProductFlatFileRepository;
import aforo.productrateplanservice.product.repository.ProductLLMTokenRepository;
import aforo.productrateplanservice.product.repository.ProductSQLResultRepository;
import aforo.productrateplanservice.product.repository.ProductStorageRepository;
import aforo.productrateplanservice.rate_plan.RatePlanRepository;
import aforo.productrateplanservice.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductStatusResolver {

    private final ProductAPIRepository productAPIRepository;
    private final ProductFlatFileRepository productFlatFileRepository;
    private final ProductSQLResultRepository productSQLResultRepository;
    private final ProductLLMTokenRepository productLLMTokenRepository;
    private final ProductStorageRepository productStorageRepository;

    private final BillableMetricClient billableMetricClient;
    private final RatePlanRepository ratePlanRepository;
    private final SubscriptionServiceClient subscriptionServiceClient;

    public ProductStatus compute(Product product) {
        if (product == null || product.getProductId() == null) {
            return ProductStatus.DRAFT;
        }

        // Basic product fields required
        if (isBlank(product.getProductName()) || isBlank(product.getInternalSkuCode())) {
            return ProductStatus.DRAFT;
        }

        Long productId = product.getProductId();
        // Exactly one complete product type config
        int completeTypes = 0;

        // API
        Optional<ProductAPI> apiOpt = productAPIRepository.findById(productId);
        if (apiOpt.isPresent()) {
            ProductAPI api = apiOpt.get();
            if (!isBlank(api.getEndpointUrl()) && api.getAuthType() != null) {
                completeTypes++;
            } else {
                return ProductStatus.DRAFT; // has API row but incomplete
            }
        }

        // FlatFile
        Optional<ProductFlatFile> ffOpt = productFlatFileRepository.findById(productId);
        if (ffOpt.isPresent()) {
            ProductFlatFile ff = ffOpt.get();
            if (!isBlank(ff.getFileLocation()) && ff.getFormat() != null) {
                completeTypes++;
            } else {
                return ProductStatus.DRAFT;
            }
        }

        // SQL Result
        Optional<ProductSQLResult> sqlOpt = productSQLResultRepository.findById(productId);
        if (sqlOpt.isPresent()) {
            ProductSQLResult sql = sqlOpt.get();
            if (!isBlank(sql.getConnectionString()) && sql.getDbType() != null && sql.getAuthType() != null) {
                completeTypes++;
            } else {
                return ProductStatus.DRAFT;
            }
        }

        // LLM Token
        Optional<ProductLLMToken> llmOpt = productLLMTokenRepository.findById(productId);
        if (llmOpt.isPresent()) {
            ProductLLMToken llm = llmOpt.get();
            if (!isBlank(llm.getModelName()) && !isBlank(llm.getEndpointUrl()) && llm.getAuthType() != null) {
                completeTypes++;
            } else {
                return ProductStatus.DRAFT;
            }
        }

        // Storage
        Optional<ProductStorage> stOpt = productStorageRepository.findById(productId);
        if (stOpt.isPresent()) {
            ProductStorage st = stOpt.get();
            if (!isBlank(st.getStorageLocation()) && st.getAuthType() != null) {
                completeTypes++;
            } else {
                return ProductStatus.DRAFT;
            }
        }

        if (completeTypes == 0) return ProductStatus.DRAFT;
        if (completeTypes > 1) return ProductStatus.DRAFT; // misconfigured

        // Configured at this point
        int activeMetrics = 0;
        try {
            activeMetrics = billableMetricClient.getMetricsByProductId(productId).size();
        } catch (Exception ignored) { /* treat as 0 */ }
        if (activeMetrics == 0) return ProductStatus.CONFIGURED;

        // Measured
        Long orgId = TenantContext.require();
        long activePlans = 0;
        try {
            activePlans = ratePlanRepository
                    .countByProduct_ProductIdAndOrganizationIdAndStatus(productId, orgId, RatePlanStatus.ACTIVE);
        } catch (Exception ignored) {
            activePlans = 0;
        }
        if (activePlans == 0) return ProductStatus.MEASURED;

        // Priced
        boolean live = false;
        try {
            live = subscriptionServiceClient.hasActiveSubscriptionForProduct(productId);
        } catch (Exception ignored) { live = false; }
        if (!live) return ProductStatus.PRICED;

        return ProductStatus.LIVE;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
