package aforo.productrateplanservice.rate_plan.service;

import aforo.productrateplanservice.cache.CacheInvalidationService;
import aforo.productrateplanservice.client.BillableMetricClient;
import aforo.productrateplanservice.exception.NotFoundException;
import aforo.productrateplanservice.exception.ValidationException;
import aforo.productrateplanservice.flatfee.FlatFeeRepository;
import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.enums.RatePlanStatus;
import aforo.productrateplanservice.product.repository.ProductRepository;
import aforo.productrateplanservice.rate_plan.*;
import aforo.productrateplanservice.stairsteppricing.StairStepPricingRepository;
import aforo.productrateplanservice.tenant.TenantContext;
import aforo.productrateplanservice.tieredpricing.TieredPricingRepository;
import aforo.productrateplanservice.usagebasedpricing.UsageBasedPricingRepository;
import aforo.productrateplanservice.volumepricing.VolumePricingRepository;
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
    
    // Pricing repositories for validation
    private final FlatFeeRepository flatFeeRepository;
    private final TieredPricingRepository tieredPricingRepository;
    private final VolumePricingRepository volumePricingRepository;
    private final UsageBasedPricingRepository usageBasedPricingRepository;
    private final StairStepPricingRepository stairStepPricingRepository;

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
     * Confirm rate plan (change status from DRAFT to ACTIVE)
     * 🔐 Requires RATE_PLAN_UPDATE permission
     */
    @Transactional
    public RatePlanDTO confirmRatePlan(Long ratePlanId) {
        Long orgId = TenantContext.require();
        
        
        RatePlan ratePlan = ratePlanRepository.findByRatePlanIdAndOrganizationId(ratePlanId, orgId)
                .orElseThrow(() -> new NotFoundException("Rate plan not found with ID: " + ratePlanId));
        
        // Validate that the rate plan is in DRAFT status
        if (ratePlan.getStatus() != RatePlanStatus.DRAFT) {
            throw new ValidationException("Rate plan can only be confirmed when in DRAFT status. Current status: " + ratePlan.getStatus());
        }
        
        // Validate that all required fields are filled
        validateRatePlanFields(ratePlan);
        
        // Validate that at least one pricing configuration exists
        validatePricingConfiguration(ratePlanId);
        
        // Change status from DRAFT to ACTIVE
        ratePlan.setStatus(RatePlanStatus.ACTIVE);
        RatePlan savedRatePlan = ratePlanRepository.save(ratePlan);
        
        // Invalidate cache
        cacheInvalidationService.invalidateAllTenantCaches(orgId);
        
        log.info("✅ Rate plan confirmed and activated: {} (ID: {})", ratePlan.getRatePlanName(), ratePlan.getRatePlanId());
        return ratePlanMapper.toDTO(savedRatePlan);
    }
    
    /**
     * Validate that all required fields are filled in the rate plan
     */
    private void validateRatePlanFields(RatePlan ratePlan) {
        if (ratePlan.getRatePlanName() == null || ratePlan.getRatePlanName().trim().isEmpty()) {
            throw new ValidationException("Rate plan name is required");
        }
        
        if (ratePlan.getBillingFrequency() == null) {
            throw new ValidationException("Billing frequency is required");
        }
        
        if (ratePlan.getPaymentType() == null) {
            throw new ValidationException("Payment type is required");
        }
        
        if (ratePlan.getProduct() == null) {
            throw new ValidationException("Product is required");
        }
    }
    
    /**
     * Validate that at least one pricing configuration exists for the rate plan
     */
    private void validatePricingConfiguration(Long ratePlanId) {
        boolean hasFlatFee = flatFeeRepository.existsByRatePlanId(ratePlanId);
        boolean hasTieredPricing = !tieredPricingRepository.findByRatePlan_RatePlanId(ratePlanId).isEmpty();
        boolean hasVolumePricing = !volumePricingRepository.findByRatePlanRatePlanId(ratePlanId).isEmpty();
        boolean hasUsageBasedPricing = !usageBasedPricingRepository.findByRatePlanRatePlanId(ratePlanId).isEmpty();
        boolean hasStairStepPricing = !stairStepPricingRepository.findByRatePlanRatePlanId(ratePlanId).isEmpty();
        
        if (!hasFlatFee && !hasTieredPricing && !hasVolumePricing && !hasUsageBasedPricing && !hasStairStepPricing) {
            throw new ValidationException("Rate plan must have at least one pricing configuration (Flat Fee, Tiered, Volume, Usage-Based, or Stair Step) before it can be confirmed");
        }
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
