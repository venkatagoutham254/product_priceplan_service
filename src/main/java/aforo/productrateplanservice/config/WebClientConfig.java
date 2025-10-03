package aforo.productrateplanservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebClientConfig {

    @Value("${clients.http.connectTimeoutMs:3000}")
    private int connectTimeoutMs;

    @Value("${clients.http.responseTimeoutMs:7000}")
    private long responseTimeoutMs;

    @Value("${clients.http.readTimeoutSec:7}")
    private int readTimeoutSec;

    @Value("${clients.http.writeTimeoutSec:7}")
    private int writeTimeoutSec;

    private WebClient build(WebClient.Builder builder, String baseUrl) {
        // Keep it simple and portable: rely on default client; timeouts can be handled at infra level
        return builder.baseUrl(baseUrl).build();
    }

    @Bean(name = "customerWebClient")
    public WebClient customerWebClient(WebClient.Builder builder) {
       return build(builder, "http://44.203.171.98:8082");  // Customer service
    }

    @Bean(name = "billableMetricWebClient")
    public WebClient billableMetricWebClient(
            WebClient.Builder builder,
            @Value("${billableMetrics.service.url}") String url) {
        return build(builder, url);
    }


    @Bean
    public WebClient ratePlanServiceWebClient(
            WebClient.Builder builder,
            @Value("${product.service.url:http://54.238.204.246:8080}") String productServiceBaseUrl) {
        return build(builder, productServiceBaseUrl + "/api/rateplans");
    }

    @Bean(name = "subscriptionWebClient")
    public WebClient subscriptionWebClient(
            WebClient.Builder builder,
            @Value("${subscriptions.service.url:http://localhost:8084}") String baseUrl) {
        return build(builder, baseUrl);
    }
}


