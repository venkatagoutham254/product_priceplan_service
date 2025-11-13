package aforo.productrateplanservice.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis", matchIfMissing = true)
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // Product cache - 30 minutes TTL
        cacheConfigurations.put("products", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        
        // Billable metrics cache - 10 minutes TTL
        cacheConfigurations.put("billableMetrics", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        
        // Rate plans cache - 15 minutes TTL
        cacheConfigurations.put("ratePlans", defaultConfig.entryTtl(Duration.ofMinutes(15)));
        
        // Pricing configurations cache - 20 minutes TTL
        cacheConfigurations.put("pricingConfigurations", defaultConfig.entryTtl(Duration.ofMinutes(20)));
        
        // Short-lived cache for validation - 5 minutes TTL
        cacheConfigurations.put("validations", defaultConfig.entryTtl(Duration.ofMinutes(5)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
