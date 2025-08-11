package aforo.productrateplanservice.client;

import aforo.productrateplanservice.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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
}
