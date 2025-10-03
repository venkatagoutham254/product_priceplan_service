package aforo.productrateplanservice.client;

import aforo.productrateplanservice.exception.ValidationException;
import aforo.productrateplanservice.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Component
public class BillableMetricClient {

    @Autowired
    @Qualifier("billableMetricWebClient")
    private WebClient webClient;

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
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            // Nothing to delete
        } catch (Exception e) {
            System.err.println(" Failed to delete billable metrics for productId " + productId + ": " + e.getMessage());
        }
    }

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
                     .block();
            return true;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (Exception e) {
            throw new ValidationException("Billable Metrics validation failed for id "
                    + id + ": " + e.getMessage());
        }
    }
    
    public List<BillableMetricResponse> getMetricsByProductId(Long productId) {
        try {
            Long orgId = TenantContext.require();
            String token = TenantContext.getJwt();
    
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/billable-metrics/by-product")
                            .queryParam("productId", productId)
                            .build())
                    .header("X-Organization-Id", String.valueOf(orgId))
                    .header("Authorization", "Bearer " + token) // 
                    .retrieve()
                    .bodyToFlux(BillableMetricResponse.class)
                    // Include all metrics regardless of status; product 'MEASURED' requires at least one metric linked
                    .filter(bm -> bm != null)
                    .collectList()
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            return List.of();
        } catch (WebClientResponseException.BadRequest e) {
            return List.of();
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
                    .block();
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
        BillableMetricResponse metric = fetchMetric(metricId);
        if (metric == null) {
            throw new ValidationException("Invalid billableMetricId: " + metricId);
        }

        String status = metric.getStatus();
        // Accept new lifecycle from UsageMetrics: CONFIGURED or beyond
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
