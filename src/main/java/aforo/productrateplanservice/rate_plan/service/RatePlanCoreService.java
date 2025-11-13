package aforo.productrateplanservice.rate_plan.service;

import aforo.productrateplanservice.cache.CacheInvalidationService;
import aforo.productrateplanservice.client.BillableMetricClient;
import aforo.productrateplanservice.exception.NotFoundException;
import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.repository.ProductRepository;
import aforo.productrateplanservice.rate_plan.*;
import aforo.productrateplanservice.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 🎯 Core Rate Plan Service - Focused on CRUD operations
 * Handles basic rate plan lifecycle without pricing configurations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RatePlanCoreService {

    private final RatePlanRepository ratePlanRepository;
    private final ProductRepository productRepository;
    private final RatePlanMapper ratePlanMapper;
    private final RatePlanAssembler ratePlanAssembler;
    private final BillableMetricClient billableMetricClient;
    private final CacheInvalidationService cacheInvalidationService;

    /**
     * Create a new rate plan (without pricing configurations)
     * 🔐 Requires RATE_PLAN_CREATE permission
     */
    @Transactional
    public RatePlanDTO createRatePlan(CreateRatePlanRequest request) {
        Long orgId = TenantContext.require();
        
        
        log.debug("Creating rate plan: {} for orgId: {}", 
                 request.getRatePlanName(), orgId);
        
        Product product = null;
        if (request.getProductId() != null) {
            Long requestedProductId = request.getProductId();
            product = productRepository
                    .findByProductIdAndOrganizationId(requestedProductId, orgId)
                    .orElseThrow(() -> new NotFoundException("Product not found with ID: " + requestedProductId));
        }
        
        // Validate billable metric if provided
        if (request.getBillableMetricId() != null) {
            Long productId = (product != null && product.getProductId() != null) ? product.getProductId() : null;
            billableMetricClient.validateActiveForProduct(request.getBillableMetricId(), productId);
        }
        
        RatePlanDTO dto = RatePlanDTO.builder()
                .ratePlanName(request.getRatePlanName())
                .description(request.getDescription())
                .billingFrequency(request.getBillingFrequency())
                .paymentType(request.getPaymentType())
                .billableMetricId(request.getBillableMetricId())
                .build();
                
        RatePlan ratePlan = ratePlanAssembler.toEntity(dto, product);
        ratePlan.setOrganizationId(orgId);
        ratePlan = ratePlanRepository.save(ratePlan);
        
        // ⚡ Invalidate related caches after creation
        cacheInvalidationService.invalidateRatePlanCaches(ratePlan.getRatePlanId());
        
        log.info("✅ Rate plan created: {} (ID: {})", ratePlan.getRatePlanName(), ratePlan.getRatePlanId());
        return ratePlanMapper.toDTO(ratePlan);
    }

    /**
     * Get basic rate plan information (without pricing configurations)
     * 🔐 Requires RATE_PLAN_READ permission
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "ratePlans", key = "'basic_' + #ratePlanId + '_' + T(aforo.productrateplanservice.tenant.TenantContext).require()")
    public RatePlanDTO getRatePlanBasic(Long ratePlanId) {
        Long orgId = TenantContext.require();
        
        
        RatePlan ratePlan = ratePlanRepository.findByRatePlanIdAndOrganizationId(ratePlanId, orgId)
                .orElseThrow(() -> new NotFoundException("Rate plan not found with ID: " + ratePlanId));
                
        ratePlan = ensureMetricStillExists(ratePlan);
        return ratePlanMapper.toDTO(ratePlan);
    }

    /**
     * Get all basic rate plans for tenant (without pricing configurations)
     * 🔐 Requires RATE_PLAN_READ permission
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "ratePlans", key = "'basic_all_' + T(aforo.productrateplanservice.tenant.TenantContext).require()")
    public List<RatePlanDTO> getAllRatePlansBasic() {
        Long orgId = TenantContext.require();
        
        
        return ratePlanRepository.findAllByOrganizationId(orgId)
                .stream()
                .map(this::ensureMetricStillExists)
                .map(ratePlanMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get rate plans by product ID (without pricing configurations)
     * 🔐 Requires RATE_PLAN_READ permission
     */
    @Transactional(readOnly = true)
    public List<RatePlanDTO> getRatePlansByProductId(Long productId) {
        Long orgId = TenantContext.require();
        
        
        return ratePlanRepository.findByProduct_ProductIdAndOrganizationId(productId, orgId)
                .stream()
                .map(this::ensureMetricStillExists)
                .map(ratePlanMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update rate plan (core fields only)
     * 🔐 Requires RATE_PLAN_UPDATE permission
     */
    @Transactional
    public RatePlanDTO updateRatePlan(Long ratePlanId, UpdateRatePlanRequest request) {
        Long orgId = TenantContext.require();
        
        
        RatePlan ratePlan = ratePlanRepository.findByRatePlanIdAndOrganizationId(ratePlanId, orgId)
                .orElseThrow(() -> new NotFoundException("Rate plan not found with ID: " + ratePlanId));

        // Update product if provided
        if (request.getProductId() != null) {
            Long requestedProductId = request.getProductId();
            Product product = productRepository
                    .findByProductIdAndOrganizationId(requestedProductId, orgId)
                    .orElseThrow(() -> new NotFoundException("Product not found with ID: " + requestedProductId));
            ratePlan.setProduct(product);
        }

        // Validate and update billable metric
        if (request.getBillableMetricId() != null) {
            Long productId = (ratePlan.getProduct() != null) ? ratePlan.getProduct().getProductId() : null;
            billableMetricClient.validateActiveForProduct(request.getBillableMetricId(), productId);
            ratePlan.setBillableMetricId(request.getBillableMetricId());
        }

        // Update core fields
        if (request.getRatePlanName() != null) {
            ratePlan.setRatePlanName(request.getRatePlanName());
        }
        if (request.getDescription() != null) {
            ratePlan.setDescription(request.getDescription());
        }
        if (request.getBillingFrequency() != null) {
            ratePlan.setBillingFrequency(request.getBillingFrequency());
        }
        if (request.getPaymentType() != null) {
            ratePlan.setPaymentType(request.getPaymentType());
        }

        ratePlan = ratePlanRepository.save(ratePlan);
        
        // ⚡ Invalidate related caches after update
        cacheInvalidationService.invalidateRatePlanCaches(ratePlanId);
        
        log.info("✅ Rate plan updated: {} (ID: {})", ratePlan.getRatePlanName(), ratePlan.getRatePlanId());
        return ratePlanMapper.toDTO(ratePlan);
    }

    /**
     * Delete rate plan
     * 🔐 Requires RATE_PLAN_DELETE permission
     */
    @Transactional
    public void deleteRatePlan(Long ratePlanId) {
        Long orgId = TenantContext.require();
        
        
        RatePlan ratePlan = ratePlanRepository.findByRatePlanIdAndOrganizationId(ratePlanId, orgId)
                .orElseThrow(() -> new NotFoundException("Rate plan not found with ID: " + ratePlanId));
        
        // ⚡ Invalidate related caches before deletion
        cacheInvalidationService.invalidateRatePlanCaches(ratePlanId);
        
        ratePlanRepository.deleteById(ratePlan.getRatePlanId());
        
        log.info("✅ Rate plan deleted: {} (ID: {})", ratePlan.getRatePlanName(), ratePlan.getRatePlanId());
    }

    /**
     * Confirm rate plan (change status)
     * 🔐 Requires RATE_PLAN_UPDATE permission
     */
    @Transactional
    public RatePlanDTO confirmRatePlan(Long ratePlanId) {
        Long orgId = TenantContext.require();
        
        
        RatePlan ratePlan = ratePlanRepository.findByRatePlanIdAndOrganizationId(ratePlanId, orgId)
                .orElseThrow(() -> new NotFoundException("Rate plan not found with ID: " + ratePlanId));
        
        // Business logic for confirmation would go here
        // For now, just return the current state
        
        log.info("✅ Rate plan confirmed: {} (ID: {})", ratePlan.getRatePlanName(), ratePlan.getRatePlanId());
        return ratePlanMapper.toDTO(ratePlan);
    }

    /**
     * Delete rate plans by billable metric ID
     * 🔐 Requires RATE_PLAN_DELETE permission
     */
    @Transactional
    public void deleteByBillableMetricId(Long billableMetricId) {
        Long orgId = TenantContext.require();
        
        
        // Note: This would need to be implemented with proper cache invalidation
        ratePlanRepository.deleteByBillableMetricIdAndOrganizationId(billableMetricId, orgId);
        
        // Invalidate all tenant caches as we don't know which rate plans were affected
        cacheInvalidationService.invalidateAllTenantCaches(orgId);
        
        log.info("✅ Rate plans deleted for billable metric ID: {}", billableMetricId);
    }

    /**
     * Ensure the billableMetricId on a rate plan still points to an existing Billable Metric.
     * If the metric was deleted in the external service, we null out the reference and persist.
     */
    private RatePlan ensureMetricStillExists(RatePlan ratePlan) {
        Long metricId = ratePlan.getBillableMetricId();
        if (metricId == null) {
            return ratePlan;
        }
        
        boolean exists;
        try {
            exists = billableMetricClient.metricExists(metricId);
        } catch (Exception e) {
            // If validation service is down, don't mutate state on reads; just return as-is
            log.warn("Could not validate billable metric existence for ID {}: {}", metricId, e.getMessage());
            return ratePlan;
        }
        
        if (!exists) {
            log.warn("Billable metric {} no longer exists, nulling reference in rate plan {}", 
                    metricId, ratePlan.getRatePlanId());
            ratePlan.setBillableMetricId(null);
            return ratePlanRepository.save(ratePlan);
        }
        
        return ratePlan;
    }
}
