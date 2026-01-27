package aforo.productrateplanservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${uploads.dir:uploads}")
    private String uploadsDir;

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                    "http://13.115.248.133",
                    "http://54.221.164.5",
                    "http://localhost:3000",
                    "http://ui.dev.aforo.space",
                    "http://product.dev.aforo.space:8080",
                    "http://metering.dev.aforo.space:8092",
                    "http://usage.dev.aforo.space:8081",
                    "http://ingestion.dev.aforo.space:8088",
                    "http://kong.dev.aforo.space:8086",
                    "http://org.dev.aforo.space:8081",
                    "http://quickbooks.dev.aforo.space:8095",
                    "http://subscription.dev.aforo.space:8084",
                    "http://*.dev.aforo.space",
                    "http://*.dev.aforo.space:*",
                    "http://*",
                    "https://*"
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Location", "Content-Type", "Authorization")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve files stored under uploadsDir at /uploads/**
        String location = "file:" + (uploadsDir.endsWith("/") || uploadsDir.endsWith("\\") ? uploadsDir : uploadsDir + "/");
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location)
                .setCachePeriod(3600);
    }
}


