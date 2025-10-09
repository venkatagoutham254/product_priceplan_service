package aforo.productrateplanservice.client;

import aforo.productrateplanservice.exception.ValidationException;
import aforo.productrateplanservice.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.time.Duration;

@Component
public class BillableMetricClient {

    @Autowired
    @Qualifier("billableMetricWebClient")
    private WebClient webClient;

    @Value("${clients.billablemetrics.timeout.sec:12}")
    private int bmTimeoutSec;

    // Helper for SpEL cache keys; avoids T(fully.qualified.Class) references
    public Long tenantId() {
        return TenantContext.get();
    }

    public void validateMetricId(Long id) {
        if (!metricExists(id)) {
            throw new ValidationException("Invalid billableMetricId: " + id);
        }
    }
    /**
     * Delete all billable metrics for the given product in the current organization.
     * Calls the internal endpoint exposed by the Billable Metrics service.
     */
    public void deleteMetricsByProductId(Long productId) {
        try {
            Long orgId = TenantContext.require();
            String token = TenantContext.getJwt();

            webClient.delete()
                    .uri("/internal/products/{productId}", productId)
                    .header("X-Organization-Id", String.valueOf(orgId))
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .toBodilessEntity()
                    .block(Duration.ofSeconds(bmTimeoutSec));
        } catch (WebClientResponseException.NotFound e) {
            // Nothing to delete
        } catch (Exception e) {
            System.err.println(" Failed to delete billable metrics for productId " + productId + ": " + e.getMessage());
        }
    }

    @Cacheable(value = "metricExists", key = "T(String).valueOf(#root.target.tenantId()) + ':' + #id")
    public boolean metricExists(Long id) {
        try {
            Long orgId = TenantContext.require();
            String token = TenantContext.getJwt();
    
            webClient.get()
                     .uri("/api/billable-metrics/{id}", id)
                     .header("X-Organization-Id", String.valueOf(orgId))
                     .header("Authorization", "Bearer " + token) // 
                     .retrieve()
                     .toBodilessEntity()
                     .block(Duration.ofSeconds(bmTimeoutSec));
            return true;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (Exception e) {
            throw new ValidationException("Billable Metrics validation failed for id "
                    + id + ": " + e.getMessage());
        }
    }
    
    @Cacheable(value = "billableMetricsByProduct", key = "T(String).valueOf(#root.target.tenantId()) + ':' + #productId", unless = "#result == null || #result.isEmpty()")
    public List<BillableMetricResponse> getMetricsByProductId(Long productId) {
        try {
            Long orgId = TenantContext.require();
            String token = TenantContext.getJwt();
            
            // Primary endpoint: by-product
            try {
                List<BillableMetricResponse> primary = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/api/billable-metrics/by-product")
                                .queryParam("productId", productId)
                                .build())
                        .header("X-Organization-Id", String.valueOf(orgId))
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .bodyToFlux(BillableMetricResponse.class)
                        .filter(bm -> bm != null && (bm.getStatus() == null || !"DRAFT".equalsIgnoreCase(bm.getStatus().trim())))
                        .collectList()
                        .block(Duration.ofSeconds(bmTimeoutSec));
                if (primary != null && !primary.isEmpty()) return primary;
            } catch (WebClientResponseException.NotFound | WebClientResponseException.BadRequest ex) {
                // fall through to legacy fallback
            }

            // Fallback: legacy list endpoint + client-side filtering by productId
            try {
                List<BillableMetricResponse> legacy = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/api/billable-metrics")
                                .build())
                        .header("X-Organization-Id", String.valueOf(orgId))
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .bodyToFlux(BillableMetricResponse.class)
                        .filter(bm -> bm != null
                                && bm.getProductId() != null && bm.getProductId().equals(productId)
                                && (bm.getStatus() == null || !"DRAFT".equalsIgnoreCase(bm.getStatus().trim())))
                        .collectList()
                        .block(Duration.ofSeconds(bmTimeoutSec));
                return legacy == null ? List.of() : legacy;
            } catch (Exception ex2) {
                System.err.println(" Failed legacy fetch for billable metrics productId " + productId + ": " + ex2.getMessage());
                return List.of();
            }
        } catch (Exception e) {
            System.err.println(" Failed to fetch billable metrics for productId " + productId + ": " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Fetch a single billable metric by id.
     * Throws ValidationException if metric is not found or response cannot be parsed.
     */
    public BillableMetricResponse fetchMetric(Long id) {
        try {
            Long orgId = TenantContext.require();
            String token = TenantContext.getJwt();

            return webClient.get()
                    .uri("/api/billable-metrics/{id}", id)
                    .header("X-Organization-Id", String.valueOf(orgId))
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(BillableMetricResponse.class)
                    .block(Duration.ofSeconds(bmTimeoutSec));
        } catch (WebClientResponseException.NotFound e) {
            throw new ValidationException("Invalid billableMetricId: " + id);
        } catch (Exception e) {
            throw new ValidationException("Failed to fetch billable metric " + id + ": " + e.getMessage());
        }
    }

    /**
     * Validate that the metric exists, is ACTIVE, and when productId is provided,
     * that the metric belongs to that product.
     */
    public void validateActiveForProduct(Long metricId, Long productId) {
        // Prefer fast path: list-by-product (already excludes DRAFT)
        if (productId != null) {
            List<BillableMetricResponse> list = getMetricsByProductId(productId);
            boolean found = list.stream()
                    .anyMatch(m -> m != null && m.getMetricId() != null && m.getMetricId().equals(metricId));
            if (found) return; // finalized and belongs to product
        }

        // Fallback: fetch by id and validate status + ownership
        BillableMetricResponse metric = fetchMetric(metricId);
        if (metric == null) {
            throw new ValidationException("Invalid billableMetricId: " + metricId);
        }
        String status = metric.getStatus();
        if (status == null) {
            throw new ValidationException("Billable metric " + metricId + " is not ready (no status)");
        }
        String st = status.trim().toUpperCase();
        if (!("CONFIGURED".equals(st) || "PRICED".equals(st) || "LIVE".equals(st))) {
            throw new ValidationException("Billable metric " + metricId + " is not finalized (status=" + status + ")");
        }
        if (productId != null && metric.getProductId() != null && !productId.equals(metric.getProductId())) {
            throw new ValidationException("Billable metric " + metricId + " does not belong to product " + productId);
        }
    }

}
