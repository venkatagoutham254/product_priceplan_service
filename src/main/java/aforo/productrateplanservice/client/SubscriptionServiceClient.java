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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Value;

@Component
public class SubscriptionServiceClient {

    @Autowired
    @Qualifier("subscriptionWebClient")
    private WebClient webClient;

    @Value("${clients.subscriptions.timeout.sec:10}")
    private int subsTimeoutSec;

    // Helper for SpEL cache keys; avoids T(fully.qualified.Class) references
    public Long tenantId() {
        return aforo.productrateplanservice.tenant.TenantContext.get();
    }

    /**
     * Returns true if there exists at least one ACTIVE subscription in subscriptionservice
     * for the given productId of the current tenant.
     */
    @Cacheable(value = "activeSubscriptionByProduct", key = "T(String).valueOf(#root.target.tenantId()) + ':' + #productId")
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
                    .block(java.time.Duration.ofSeconds(subsTimeoutSec));

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

    /**
     * Fetch all ACTIVE subscriptions for the current tenant and return the set of productIds.
     * Used to avoid N+1 remote calls when computing status across many products.
     */
    @Cacheable(value = "activeSubscriptionProductIds", key = "T(String).valueOf(#root.target.tenantId())")
    public java.util.Set<Long> fetchActiveSubscriptionProductIds() {
        try {
            Long orgId = TenantContext.require();
            String token = TenantContext.getJwt();

            String json = webClient.get()
                    .uri("/api/subscriptions")
                    .header("X-Organization-Id", String.valueOf(orgId))
                    .header("Authorization", token != null ? ("Bearer " + token) : "")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(java.time.Duration.ofSeconds(subsTimeoutSec));

            java.util.Set<Long> result = new java.util.HashSet<>();
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
            return java.util.Set.of();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Failed to fetch subscriptions", e);
        }
    }
}
