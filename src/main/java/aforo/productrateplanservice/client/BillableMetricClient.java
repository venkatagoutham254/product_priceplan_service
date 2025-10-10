package aforo.productrateplanservice.client;

import aforo.productrateplanservice.exception.ValidationException;
import aforo.productrateplanservice.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class BillableMetricClient {

    @Autowired
    @Qualifier("billableMetricWebClient")
    private WebClient webClient;

    @Value("${clients.billablemetrics.timeout.sec:2}") // default to 2s (fail fast)
    private int bmTimeoutSec;

    public Long tenantId() { return TenantContext.get(); }

    public String idsKey(Collection<Long> ids) {
        if (ids == null) return "[]";
        return ids.stream().filter(Objects::nonNull).sorted().map(String::valueOf).collect(Collectors.joining(","));
    }

    /**
     * Batch fetch metrics. Best-effort, short timeout. No "download all" fallback.
     * Prefers POST with JSON body of ids to avoid huge query strings.
     */
    public Map<Long, List<BillableMetricResponse>> getMetricsForProducts(Collection<Long> productIds) {
        try {
            Long orgId = TenantContext.require();
            String token = TenantContext.getJwt();
            if (productIds == null || productIds.isEmpty()) return Map.of();
            Set<Long> ids = productIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());
            if (ids.isEmpty()) return Map.of();

            // Try POST batch endpoint (recommended)
            List<BillableMetricResponse> batch = webClient.post()
                    .uri("/api/billable-metrics/by-product-ids")
                    .header("X-Organization-Id", String.valueOf(orgId))
                    .header("Authorization", "Bearer " + token)
                    .bodyValue(ids)
                    .retrieve()
                    .bodyToFlux(BillableMetricResponse.class)
                    .filter(bm -> bm != null
                            && bm.getProductId() != null && ids.contains(bm.getProductId())
                            && (bm.getStatus() == null || !"DRAFT".equalsIgnoreCase(bm.getStatus().trim())))
                    .collectList()
                    .block(Duration.ofSeconds(bmTimeoutSec));

            if (batch == null || batch.isEmpty()) return Map.of();
            return batch.stream().collect(Collectors.groupingBy(BillableMetricResponse::getProductId));

        } catch (Exception e) {
            // Fail fast: return empty on any issue
            return Map.of();
        }
    }

    public void validateMetricId(Long id) {
        if (!metricExists(id)) {
            throw new ValidationException("Invalid billableMetricId: " + id);
        }
    }

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
            // ignore
        } catch (Exception e) {
            // best-effort delete
        }
    }

    public boolean metricExists(Long id) {
        try {
            Long orgId = TenantContext.require();
            String token = TenantContext.getJwt();

            webClient.get()
                    .uri("/api/billable-metrics/{id}", id)
                    .header("X-Organization-Id", String.valueOf(orgId))
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .toBodilessEntity()
                    .block(Duration.ofSeconds(bmTimeoutSec));
            return true;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (Exception e) {
            throw new ValidationException("Billable Metrics validation failed for id " + id + ": " + e.getMessage());
        }
    }

    public List<BillableMetricResponse> getMetricsByProductId(Long productId) {
        try {
            Long orgId = TenantContext.require();
            String token = TenantContext.getJwt();

            List<BillableMetricResponse> list = webClient.get()
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

            return list == null ? List.of() : list;
        } catch (Exception e) {
            return List.of(); // best-effort
        }
    }

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

    public void validateActiveForProduct(Long metricId, Long productId) {
        if (productId != null) {
            List<BillableMetricResponse> list = getMetricsByProductId(productId);
            boolean found = list.stream()
                    .anyMatch(m -> m != null && m.getMetricId() != null && m.getMetricId().equals(metricId));
            if (found) return;
        }
        BillableMetricResponse metric = fetchMetric(metricId);
        if (metric == null) throw new ValidationException("Invalid billableMetricId: " + metricId);
        String st = (metric.getStatus() == null ? "" : metric.getStatus().trim().toUpperCase());
        if (!("CONFIGURED".equals(st) || "PRICED".equals(st) || "LIVE".equals(st))) {
            throw new ValidationException("Billable metric " + metricId + " is not finalized (status=" + metric.getStatus() + ")");
        }
        if (productId != null && metric.getProductId() != null && !productId.equals(metric.getProductId())) {
            throw new ValidationException("Billable metric " + metricId + " does not belong to product " + productId);
        }
    }
}
