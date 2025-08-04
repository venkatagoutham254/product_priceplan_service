package aforo.productrateplanservice.client;

import aforo.productrateplanservice.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class BillableMetricClient {

    private final WebClient billableMetricWebClient;

    public boolean metricExists(Long id) {
        try {
            BillableMetricResponse[] response = billableMetricWebClient
                    .get()
                    .uri("/api/billable-metrics")
                    .retrieve()
                    .bodyToMono(BillableMetricResponse[].class)
                    .block();

            if (response == null) return false;

            for (BillableMetricResponse metric : response) {
                if (metric.getMetricId().equals(id)) {
                    return true;
                }
            }
            return false;

        } catch (Exception e) {
            return false;
        }
    }

    // 🔴 Add this method — it is missing!
    public void validateMetricId(Long id) {
        if (!metricExists(id)) {
            throw new ValidationException("Invalid billableMetricId: " + id);
        }
    }
}
