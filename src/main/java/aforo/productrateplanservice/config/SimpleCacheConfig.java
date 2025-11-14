package aforo.productrateplanservice.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class SimpleCacheConfig {

    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        
        // Set up the same cache names as used in the application
        cacheManager.setCacheNames(java.util.Arrays.asList(
            "products",
            "billableMetrics", 
            "ratePlans",
            "pricingConfigurations",
            "validations"
        ));
        
        return cacheManager;
    }
}
