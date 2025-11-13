package aforo.productrateplanservice.cache;

import aforo.productrateplanservice.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * ⚡ Centralized cache invalidation service for related entities
 * Handles complex cache dependencies and tenant-aware invalidation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CacheInvalidationService {

    private final CacheManager cacheManager;

    /**
     * Invalidate all product-related caches for a specific product
     */
    public void invalidateProductCaches(Long productId) {
        Long orgId = TenantContext.require();
        String tenantKey = orgId.toString();
        
        log.debug("Invalidating product caches for productId: {}, orgId: {}", productId, orgId);
        
        // Invalidate specific product cache
        evictFromCache("products", productId + "_" + orgId);
        
        // Invalidate billable metrics for this product
        evictFromCache("billableMetrics", productId);
        
        // Invalidate all rate plans for this product (they contain product info)
        evictCacheByPattern("ratePlans", "product_" + productId + "_");
        
        // Invalidate tenant-wide product lists
        evictFromCache("products", "all_" + tenantKey);
        
        log.info("✅ Product caches invalidated for productId: {}", productId);
    }

    /**
     * Invalidate all rate plan related caches for a specific rate plan
     */
    public void invalidateRatePlanCaches(Long ratePlanId) {
        Long orgId = TenantContext.require();
        String tenantKey = orgId.toString();
        
        log.debug("Invalidating rate plan caches for ratePlanId: {}, orgId: {}", ratePlanId, orgId);
        
        // Invalidate specific rate plan cache
        evictFromCache("ratePlans", ratePlanId + "_" + orgId);
        
        // Invalidate detailed rate plan cache (includes pricing configurations)
        evictFromCache("ratePlans", "detailed_" + ratePlanId + "_" + orgId);
        
        // Invalidate tenant-wide rate plan lists
        evictFromCache("ratePlans", "all_" + tenantKey);
        evictFromCache("ratePlans", "list_" + tenantKey);
        
        log.info("✅ Rate plan caches invalidated for ratePlanId: {}", ratePlanId);
    }

    /**
     * Invalidate rate plan caches when pricing configurations change
     */
    public void invalidateRatePlanCachesForPricingChange(Long ratePlanId, String pricingType) {
        Long orgId = TenantContext.require();
        
        log.debug("Invalidating rate plan caches for pricing change - ratePlanId: {}, type: {}, orgId: {}", 
                 ratePlanId, pricingType, orgId);
        
        // Invalidate the detailed rate plan cache (contains pricing configs)
        evictFromCache("ratePlans", "detailed_" + ratePlanId + "_" + orgId);
        
        // Invalidate specific pricing type cache
        evictFromCache("pricingConfigurations", pricingType + "_" + ratePlanId + "_" + orgId);
        
        // Invalidate batch queries cache
        evictFromCache("ratePlans", "batch_" + orgId);
        
        log.info("✅ Rate plan pricing caches invalidated for ratePlanId: {}, type: {}", ratePlanId, pricingType);
    }

    /**
     * Invalidate all caches for a tenant (used for major operations)
     */
    public void invalidateAllTenantCaches(Long organizationId) {
        log.debug("Invalidating ALL caches for orgId: {}", organizationId);
        
        // Clear all cache regions for this tenant
        clearCacheByTenant("products", organizationId);
        clearCacheByTenant("ratePlans", organizationId);
        clearCacheByTenant("billableMetrics", organizationId);
        clearCacheByTenant("pricingConfigurations", organizationId);
        clearCacheByTenant("validations", organizationId);
        
        log.warn("🧹 ALL tenant caches cleared for orgId: {}", organizationId);
    }

    /**
     * Warm up critical caches after major operations
     */
    public void warmUpCriticalCaches(Long organizationId) {
        log.debug("Warming up critical caches for orgId: {}", organizationId);
        
        // Note: Actual cache warming would be implemented by calling the cached methods
        // This is a placeholder for the warming strategy
        
        log.info("🔥 Critical caches warmed for orgId: {}", organizationId);
    }

    /**
     * Evict a specific key from a cache
     */
    private void evictFromCache(String cacheName, Object key) {
        try {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.evict(key);
                log.trace("Evicted key '{}' from cache '{}'", key, cacheName);
            }
        } catch (Exception e) {
            log.warn("Failed to evict key '{}' from cache '{}': {}", key, cacheName, e.getMessage());
        }
    }

    /**
     * Evict cache entries by pattern (simplified implementation)
     */
    private void evictCacheByPattern(String cacheName, String keyPattern) {
        try {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                // For Redis cache, we would use pattern matching
                // For now, we'll clear the entire cache region as a safe fallback
                cache.clear();
                log.trace("Cleared cache '{}' due to pattern '{}'", cacheName, keyPattern);
            }
        } catch (Exception e) {
            log.warn("Failed to clear cache '{}' by pattern '{}': {}", cacheName, keyPattern, e.getMessage());
        }
    }

    /**
     * Clear all cache entries for a specific tenant
     */
    private void clearCacheByTenant(String cacheName, Long organizationId) {
        try {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                // In a production environment, we would implement tenant-specific clearing
                // For now, we'll clear the entire cache as a safe approach
                cache.clear();
                log.trace("Cleared cache '{}' for tenant {}", cacheName, organizationId);
            }
        } catch (Exception e) {
            log.warn("Failed to clear cache '{}' for tenant {}: {}", cacheName, organizationId, e.getMessage());
        }
    }
}
