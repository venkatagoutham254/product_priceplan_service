package aforo.productrateplanservice.client;

import aforo.productrateplanservice.exception.ValidationException;
import aforo.productrateplanservice.client.BillableMetricResponse;
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

    public boolean metricExists(Long id) {
        try {
            webClient.get()
                     .uri("/api/billable-metrics/{id}", id)
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
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/billable-metrics/by-product")
                            .queryParam("productId", productId)
                            .build())
                    .retrieve()
                    .bodyToFlux(BillableMetricResponse.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            // no metrics linked for this product
            return List.of();
        } catch (WebClientResponseException.BadRequest e) {
            // invalid param → treat as no metrics
            return List.of();
        } catch (Exception e) {
            // unexpected error → log and return empty instead of breaking product
            System.err.println("⚠️ Failed to fetch billable metrics for productId " + productId + ": " + e.getMessage());
            return List.of();
        }
    }
    
}
