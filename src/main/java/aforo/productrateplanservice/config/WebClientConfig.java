package aforo.productrateplanservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean(name = "customerWebClient")
    public WebClient customerWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://44.203.171.98:8082")  // Customer service
                .build();
    }

    @Bean(name = "billableMetricWebClient")
    public WebClient billableMetricWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8085") // Billable Metrics service
                .build();
    }
}
