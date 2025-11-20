package aforo.productrateplanservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebClientConfig {

    @Bean(name = "customerWebClient")
    public WebClient customerWebClient(
            WebClient.Builder builder,
            @Value("${customer.service.url}") String url) {
        return builder.baseUrl(url).build();
    }

    @Bean(name = "billableMetricWebClient")
    public WebClient billableMetricWebClient(
            WebClient.Builder builder,
            @Value("${billableMetrics.service.url}") String url) {
        return builder.baseUrl(url).build();
    }


    @Bean
public WebClient ratePlanServiceWebClient() {
    // Update this line - change /rate-plans to /rateplans
    return WebClient.builder()
            .baseUrl("http://54.238.204.246:8080/api/rateplans")  // Fixed path
            .build();
}
}
