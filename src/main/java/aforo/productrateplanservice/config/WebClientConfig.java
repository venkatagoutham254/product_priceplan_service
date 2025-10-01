package aforo.productrateplanservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebClientConfig {

    @Bean(name = "customerWebClient")
    public WebClient customerWebClient(WebClient.Builder builder) {
       return builder
                .baseUrl("http://44.203.171.98:8082")  // Customer service
                .build();
    }

    @Bean(name = "billableMetricWebClient")
    public WebClient billableMetricWebClient(
            WebClient.Builder builder,
            @Value("${billableMetrics.service.url}") String url) {
        return builder.baseUrl(url).build();
    }


    @Bean
    public WebClient ratePlanServiceWebClient(
            WebClient.Builder builder,
            @Value("${product.service.url:http://54.238.204.246:8080}") String productServiceBaseUrl) {
        return builder
                .baseUrl(productServiceBaseUrl + "/api/rateplans")
                .build();
    }

    @Bean(name = "subscriptionWebClient")
    public WebClient subscriptionWebClient(
            WebClient.Builder builder,
            @Value("${subscriptions.service.url:http://localhost:8084}") String baseUrl) {
        return builder.baseUrl(baseUrl).build();
    }
}


