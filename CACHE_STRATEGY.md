# 🚀 Cache Invalidation Strategy

## Overview

This document outlines the comprehensive cache invalidation strategy implemented for the Product Rate Plan Service. The strategy ensures data consistency while maximizing performance through intelligent cache management.

## 🎯 Cache Architecture

### Cache Regions

| Cache Name | TTL | Purpose | Key Pattern |
|------------|-----|---------|-------------|
| `products` | 30 min | Product data | `{productId}_{orgId}`, `all_{orgId}` |
| `ratePlans` | 15 min | Rate plan data | `detailed_{ratePlanId}_{orgId}`, `all_{orgId}` |
| `billableMetrics` | 10 min | External metrics | `{productId}` |
| `pricingConfigurations` | 20 min | Pricing configs | `{type}_{ratePlanId}_{orgId}` |
| `validations` | 5 min | Validation results | Various patterns |

### Tenant Isolation

All cache keys include the organization ID (`orgId`) to ensure complete tenant isolation:
- ✅ **Secure**: No cross-tenant data leakage
- ✅ **Scalable**: Independent cache management per tenant
- ✅ **Efficient**: Tenant-specific invalidation

## 🔧 Invalidation Strategies

### 1. Product Cache Invalidation

**Triggers:**
- Product creation, update, deletion
- Product-related rate plan changes

**Invalidated Caches:**
```java
// Specific product
products:{productId}_{orgId}

// Related billable metrics
billableMetrics:{productId}

// Related rate plans
ratePlans:product_{productId}_*

// Tenant-wide lists
products:all_{orgId}
```

### 2. Rate Plan Cache Invalidation

**Triggers:**
- Rate plan creation, update, deletion
- Pricing configuration changes

**Invalidated Caches:**
```java
// Specific rate plan
ratePlans:{ratePlanId}_{orgId}
ratePlans:detailed_{ratePlanId}_{orgId}

// Tenant-wide lists
ratePlans:all_{orgId}
ratePlans:list_{orgId}
```

### 3. Pricing Configuration Invalidation

**Triggers:**
- Tiered pricing changes
- Volume pricing changes
- Usage-based pricing changes
- Stair step pricing changes

**Invalidated Caches:**
```java
// Detailed rate plan (contains pricing configs)
ratePlans:detailed_{ratePlanId}_{orgId}

// Specific pricing type
pricingConfigurations:{type}_{ratePlanId}_{orgId}

// Batch queries cache
ratePlans:batch_{orgId}
```

## 📋 Implementation Details

### CacheInvalidationService

Central service managing all cache invalidation logic:

```java
@Service
public class CacheInvalidationService {
    
    // Product-related invalidation
    public void invalidateProductCaches(Long productId)
    
    // Rate plan invalidation
    public void invalidateRatePlanCaches(Long ratePlanId)
    
    // Pricing configuration invalidation
    public void invalidateRatePlanCachesForPricingChange(Long ratePlanId, String pricingType)
    
    // Emergency: clear all tenant caches
    public void invalidateAllTenantCaches(Long organizationId)
    
    // Performance: warm critical caches
    public void warmUpCriticalCaches(Long organizationId)
}
```

### Service Integration

**Rate Plan Service:**
```java
@Cacheable(value = "ratePlans", key = "'all_' + T(aforo.productrateplanservice.tenant.TenantContext).require()")
public List<RatePlanDTO> getAllRatePlans()

@Cacheable(value = "ratePlans", key = "'detailed_' + #ratePlanId + '_' + T(aforo.productrateplanservice.tenant.TenantContext).require()")
public RatePlanDTO getRatePlanById(Long ratePlanId)
```

**Pricing Services:**
```java
// After pricing configuration changes
cacheInvalidationService.invalidateRatePlanCachesForPricingChange(ratePlanId, "TIERED_PRICING");
```

## 🔄 Cache Lifecycle

### 1. Cache Population
- **On-demand**: Caches populated when data is first requested
- **Batch optimization**: Use optimized queries to prevent N+1 problems
- **Tenant-aware**: All keys include organization context

### 2. Cache Invalidation
- **Immediate**: Synchronous invalidation on data changes
- **Granular**: Only invalidate affected cache entries
- **Cascading**: Related caches invalidated automatically

### 3. Cache Warming (Future Enhancement)
- **Critical data**: Pre-populate frequently accessed data
- **Background**: Asynchronous warming to avoid blocking operations
- **Smart**: Based on usage patterns and tenant activity

## 🚨 Emergency Procedures

### Complete Cache Clear
```java
// Nuclear option: clear all caches for a tenant
cacheInvalidationService.invalidateAllTenantCaches(organizationId);
```

### Cache Monitoring
- **Metrics**: Track cache hit/miss ratios
- **Alerts**: Monitor cache performance degradation
- **Logging**: Detailed invalidation logging for debugging

## 📊 Performance Impact

### Before Cache Invalidation Strategy
- ❌ **Stale data**: Inconsistent cache state
- ❌ **Manual clearing**: No systematic invalidation
- ❌ **Over-invalidation**: Clearing entire cache regions

### After Cache Invalidation Strategy
- ✅ **Data consistency**: Always fresh data
- ✅ **Granular control**: Precise cache invalidation
- ✅ **Performance**: Optimal cache utilization

### Expected Improvements
- **Cache hit ratio**: 85-95% for frequently accessed data
- **Data freshness**: < 1 second staleness
- **Performance**: 50-80% faster response times for cached data

## 🔧 Configuration

### Redis Configuration
```yaml
spring:
  cache:
    type: redis
    redis:
      time-to-live: 600000  # 10 minutes default
```

### Cache-Specific TTL
- **Products**: 30 minutes (relatively stable)
- **Rate Plans**: 15 minutes (moderate changes)
- **Billable Metrics**: 10 minutes (external data)
- **Pricing Configs**: 20 minutes (business critical)
- **Validations**: 5 minutes (short-lived)

## 🛠️ Troubleshooting

### Common Issues

1. **Cache Not Invalidating**
   - Check tenant context in cache keys
   - Verify invalidation service is called
   - Review cache key patterns

2. **Performance Degradation**
   - Monitor cache hit ratios
   - Check for cache stampede scenarios
   - Review TTL configurations

3. **Memory Usage**
   - Monitor Redis memory consumption
   - Implement cache size limits
   - Consider cache eviction policies

### Debug Commands
```bash
# Check cache contents (Redis CLI)
redis-cli KEYS "ratePlans:*"
redis-cli GET "ratePlans:detailed_123_456"

# Monitor cache operations
redis-cli MONITOR
```

## 🚀 Future Enhancements

### Phase 1 (Completed)
- ✅ Basic cache invalidation
- ✅ Tenant-aware keys
- ✅ Service integration

### Phase 2 (Planned)
- 🔄 **Smart warming**: Predictive cache population
- 🔄 **Event-driven**: Async invalidation via events
- 🔄 **Metrics**: Comprehensive cache monitoring

### Phase 3 (Future)
- 🔄 **ML-based**: Machine learning for cache optimization
- 🔄 **Distributed**: Multi-region cache coordination
- 🔄 **Real-time**: WebSocket-based cache updates

## 📚 References

- [Spring Cache Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#cache)
- [Redis Best Practices](https://redis.io/docs/manual/patterns/)
- [Cache Invalidation Patterns](https://martinfowler.com/bliki/TwoHardThings.html)
