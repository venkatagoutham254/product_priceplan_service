package aforo.productrateplanservice.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import java.time.Duration;

@Component
public class CustomerServiceClient {
    private final WebClient webClient;

    @Autowired
    public CustomerServiceClient(@Qualifier("customerWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public boolean validateCustomerId(Long customerId) {
        try {
            webClient.get()
                    .uri("/v1/api/customers/{customerId}", customerId)
                    .retrieve()
                    .toBodilessEntity()
                    .block(Duration.ofSeconds(7));

            return true;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (WebClientResponseException.InternalServerError e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to validate customerId");
        }
    }
    
    public boolean validateOrganizationId(Long organizationId) {
        try {
            webClient.get()
                    .uri("/v1/api/organizations/{organizationId}", organizationId)
                    .retrieve()
                    .toBodilessEntity()
                    .block(Duration.ofSeconds(7));

            return true;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to validate organizationId");
        }
    }

    public boolean validateDivisionId(Long divisionId) {
        try {
            webClient.get()
                    .uri("/v1/api/divisions/{divisionId}", divisionId)
                    .retrieve()
                    .toBodilessEntity()
                    .block(Duration.ofSeconds(7));

            return true;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to validate divisionId");
        }
    }
}
