package aforo.productrateplanservice.client;

import aforo.productrateplanservice.exception.ValidationException;
import aforo.productrateplanservice.client.BillableMetricResponse;
import aforo.productrateplanservice.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.stream.Collectors;

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

    public boolean metricExists(Long id) {
        try {
            Long orgId = TenantContext.require();
            String token = TenantContext.getJwt();
    
            webClient.get()
                     .uri("/api/billable-metrics/{id}", id)
                     .header("X-Organization-Id", String.valueOf(orgId))
                     .header("Authorization", "Bearer " + token) // 👈 forward token
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
                    .header("Authorization", "Bearer " + token) // 👈 forward token
                    .retrieve()
                    .bodyToFlux(BillableMetricResponse.class)
                    .filter(bm -> bm != null && bm.getStatus() != null && bm.getStatus().equalsIgnoreCase("ACTIVE"))
                    .collectList()
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            return List.of();
        } catch (WebClientResponseException.BadRequest e) {
            return List.of();
        } catch (Exception e) {
            System.err.println("⚠️ Failed to fetch billable metrics for productId " + productId + ": " + e.getMessage());
            return List.of();
        }
    }
    
    
}
