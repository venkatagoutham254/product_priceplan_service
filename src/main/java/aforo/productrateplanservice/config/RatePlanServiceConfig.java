package aforo.productrateplanservice.config;

import aforo.productrateplanservice.rate_plan.RatePlanService;
import aforo.productrateplanservice.rate_plan.RatePlanServiceImplRefactored;
import aforo.productrateplanservice.rate_plan.service.RatePlanCoreService;
import aforo.productrateplanservice.rate_plan.service.RatePlanPricingAggregationService;
import aforo.productrateplanservice.rate_plan.RatePlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration; // Temporarily disabled
import org.springframework.context.annotation.Primary;

/**
 * 🏗️ Rate Plan Service Configuration
 * 
 * Allows switching between the original god service and the refactored architecture
 * via application properties for gradual migration and A/B testing.
 * 
 * TEMPORARILY DISABLED - Using @Service annotation instead
 */
// @Configuration
@Slf4j
public class RatePlanServiceConfig {

    /**
     * 🚀 NEW: Refactored Rate Plan Service (Default)
     * Uses focused services following SOLID principles
     */
    @Bean
    @Primary
    @ConditionalOnProperty(
        name = "app.rate-plan.service.implementation", 
        havingValue = "refactored", 
        matchIfMissing = true
    )
    public RatePlanService ratePlanServiceRefactored(
            RatePlanCoreService coreService,
            RatePlanPricingAggregationService pricingService,
            RatePlanRepository repository) {
        
        log.info("🏗️ Configuring REFACTORED Rate Plan Service (3 focused dependencies)");
        return new RatePlanServiceImplRefactored(coreService, pricingService, repository);
    }

    /**
     * 🔄 LEGACY: Original God Service (DISABLED)
     * The legacy god service has been deprecated and disabled.
     * Use the refactored architecture instead.
     */
    @Bean("ratePlanServiceLegacyDisabled")
    @ConditionalOnProperty(
        name = "app.rate-plan.service.implementation", 
        havingValue = "legacy"
    )
    public RatePlanService ratePlanServiceLegacyDisabled() {
        log.error("🚨 LEGACY Rate Plan Service is DEPRECATED and DISABLED!");
        log.error("🚨 Please use 'refactored' implementation instead.");
        log.error("🚨 Update application.yml: app.rate-plan.service.implementation=refactored");
        
        throw new UnsupportedOperationException(
            "Legacy god service has been deprecated. Use 'refactored' implementation instead. " +
            "The legacy service had 20+ dependencies and violated SOLID principles. " +
            "The refactored version provides better performance, maintainability, and testability."
        );
    }
}
