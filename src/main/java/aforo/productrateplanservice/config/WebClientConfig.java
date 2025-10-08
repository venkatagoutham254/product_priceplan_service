package aforo.productrateplanservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

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
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
                .responseTimeout(Duration.ofMillis(responseTimeoutMs))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(readTimeoutSec))
                        .addHandlerLast(new WriteTimeoutHandler(writeTimeoutSec))
                );

        return builder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(baseUrl)
                .build();
    }

    @Bean(name = "customerWebClient")
    public WebClient customerWebClient(
            WebClient.Builder builder,
            @Value("${customer.service.url}") String url) {
       return build(builder, url);
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


