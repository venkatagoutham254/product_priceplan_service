package aforo.productrateplanservice.client;

import aforo.productrateplanservice.tenant.TenantContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class SubscriptionServiceClient {

    @Autowired
    @Qualifier("subscriptionWebClient")
    private WebClient webClient;

    /**
     * Returns true if there exists at least one ACTIVE subscription in subscriptionservice
     * for the given productId of the current tenant.
     */
    public boolean hasActiveSubscriptionForProduct(Long productId) {
        try {
            Long orgId = TenantContext.require();
            String token = TenantContext.getJwt();

            String json = webClient.get()
                    .uri("/api/subscriptions")
                    .header("X-Organization-Id", String.valueOf(orgId))
                    .header("Authorization", token != null ? ("Bearer " + token) : "")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

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
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Failed to check product subscriptions", e);
        }
    }
}
