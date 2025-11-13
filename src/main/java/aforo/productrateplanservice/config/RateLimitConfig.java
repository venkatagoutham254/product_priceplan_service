package aforo.productrateplanservice.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig {
    private final ConcurrentHashMap<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    public Bucket createBucket(String key, RateLimitType type) {
        return bucketCache.computeIfAbsent(key, k -> {
            return switch (type) {
                case GENERAL_API -> Bucket.builder()
                        .addLimit(Bandwidth.simple(100, Duration.ofMinutes(1))) // 100 requests per minute
                        .addLimit(Bandwidth.simple(1000, Duration.ofHours(1)))  // 1000 requests per hour
                        .build();
                case HEAVY_OPERATIONS -> Bucket.builder()
                        .addLimit(Bandwidth.simple(10, Duration.ofMinutes(1)))  // 10 requests per minute
                        .addLimit(Bandwidth.simple(50, Duration.ofHours(1)))    // 50 requests per hour
                        .build();
                case ADMIN_OPERATIONS -> Bucket.builder()
                        .addLimit(Bandwidth.simple(5, Duration.ofMinutes(1)))   // 5 requests per minute
                        .addLimit(Bandwidth.simple(20, Duration.ofHours(1)))    // 20 requests per hour
                        .build();
            };
        });
    }

    public enum RateLimitType {
        GENERAL_API,
        HEAVY_OPERATIONS,
        ADMIN_OPERATIONS
    }
}
