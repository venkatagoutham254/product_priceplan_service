package aforo.productrateplanservice.client;

import aforo.productrateplanservice.tenant.TenantContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Component
public class SubscriptionServiceClient {

    @Autowired
    @Qualifier("subscriptionWebClient")
    private WebClient webClient;

    @Value("${clients.subscriptions.timeout.sec:2}") // default to 2s
    private int subsTimeoutSec;

    public Long tenantId() { return TenantContext.get(); }

    public boolean hasActiveSubscriptionForProduct(Long productId) {
        try {
            Long orgId = TenantContext.require();
            String token = TenantContext.getJwt();

            String json = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/subscriptions")
                            .queryParam("status", "ACTIVE") // use filter if backend supports it
                            .build())
                    .header("X-Organization-Id", String.valueOf(orgId))
                    .header("Authorization", token != null ? ("Bearer " + token) : "")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(subsTimeoutSec));

            if (json == null || json.isBlank()) return false;

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(json);
            if (!node.isArray()) return false;

            for (JsonNode item : node) {
                long pid = item.path("productId").asLong(-1);
                String status = item.path("status").asText("");
                if (pid == (productId == null ? -2 : productId) && "ACTIVE".equalsIgnoreCase(status)) {
                    return true;
                }
            }
            return false;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (Exception e) {
            // fail fast
            return false;
        }
    }

    public Set<Long> fetchActiveSubscriptionProductIds() {
        try {
            Long orgId = TenantContext.require();
            String token = TenantContext.getJwt();

            String json = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/subscriptions")
                            .queryParam("status", "ACTIVE") // if unsupported, backend will ignore but we still cap time
                            .build())
                    .header("X-Organization-Id", String.valueOf(orgId))
                    .header("Authorization", token != null ? ("Bearer " + token) : "")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(subsTimeoutSec));

            Set<Long> result = new HashSet<>();
            if (json == null || json.isBlank()) return result;

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(json);
            if (!node.isArray()) return result;

            for (JsonNode item : node) {
                long pid = item.path("productId").asLong(-1);
                String status = item.path("status").asText("");
                if (pid > 0 && "ACTIVE".equalsIgnoreCase(status)) {
                    result.add(pid);
                }
            }
            return result;
        } catch (WebClientResponseException.NotFound e) {
            return Set.of();
        } catch (Exception e) {
            // fail fast
            return Set.of();
        }
    }
}
