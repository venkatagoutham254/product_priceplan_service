package aforo.productrateplanservice.rate_plan;

import aforo.productrateplanservice.flatfee.FlatFeeRepository;
import aforo.productrateplanservice.tieredpricing.TieredPricingRepository;
import aforo.productrateplanservice.volumepricing.VolumePricingRepository;
import aforo.productrateplanservice.usagebasedpricing.UsageBasedPricingRepository;
import aforo.productrateplanservice.stairsteppricing.StairStepPricingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Centralized service for managing pricing configurations.
 * This service owns the business logic for clearing conflicting pricing types.
 * 
 * Business Rule: A rate plan can only have ONE type of pricing configuration at a time.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PricingConfigurationService {

    private final FlatFeeRepository flatFeeRepository;
    private final TieredPricingRepository tieredPricingRepository;
    private final VolumePricingRepository volumePricingRepository;
    private final UsageBasedPricingRepository usageBasedPricingRepository;
    private final StairStepPricingRepository stairStepPricingRepository;

    /**
     * Clear all pricing configurations for a rate plan except the specified type.
     * This ensures only one pricing type is active at a time.
     */
    @Transactional
    public void clearOtherPricingConfigurations(Long ratePlanId, PricingType keepType) {
        log.info("Clearing pricing configurations for ratePlanId: {}, keeping type: {}", ratePlanId, keepType);
        
        if (keepType != PricingType.FLAT_FEE) {
            clearFlatFee(ratePlanId);
        }
        
        if (keepType != PricingType.TIERED_PRICING) {
            clearTieredPricings(ratePlanId);
        }
        
        if (keepType != PricingType.VOLUME_PRICING) {
            clearVolumePricings(ratePlanId);
        }
        
        if (keepType != PricingType.USAGE_BASED_PRICING) {
            clearUsageBasedPricings(ratePlanId);
        }
        
        if (keepType != PricingType.STAIR_STEP_PRICING) {
            clearStairStepPricings(ratePlanId);
        }
        
        log.info("Completed clearing pricing configurations for ratePlanId: {}", ratePlanId);
    }

    /**
     * Clear ALL pricing configurations for a rate plan.
     * Used when deleting a rate plan or resetting all pricing.
     */
    @Transactional
    public void clearAllPricingConfigurations(Long ratePlanId) {
        log.info("Clearing ALL pricing configurations for ratePlanId: {}", ratePlanId);
        
        clearFlatFee(ratePlanId);
        clearTieredPricings(ratePlanId);
        clearVolumePricings(ratePlanId);
        clearUsageBasedPricings(ratePlanId);
        clearStairStepPricings(ratePlanId);
        
        log.info("Completed clearing ALL pricing configurations for ratePlanId: {}", ratePlanId);
    }

    private void clearFlatFee(Long ratePlanId) {
        flatFeeRepository.findByRatePlanId(ratePlanId).ifPresent(flatFee -> {
            log.debug("Deleting flat fee for ratePlanId: {}", ratePlanId);
            flatFeeRepository.delete(flatFee);
        });
    }

    private void clearTieredPricings(Long ratePlanId) {
        var tieredPricings = tieredPricingRepository.findByRatePlan_RatePlanId(ratePlanId);
        if (!tieredPricings.isEmpty()) {
            log.debug("Deleting {} tiered pricings for ratePlanId: {}", tieredPricings.size(), ratePlanId);
            tieredPricingRepository.deleteAll(tieredPricings);
        }
    }

    private void clearVolumePricings(Long ratePlanId) {
        var volumePricings = volumePricingRepository.findByRatePlanRatePlanId(ratePlanId);
        if (!volumePricings.isEmpty()) {
            log.debug("Deleting {} volume pricings for ratePlanId: {}", volumePricings.size(), ratePlanId);
            volumePricingRepository.deleteAll(volumePricings);
        }
    }

    private void clearUsageBasedPricings(Long ratePlanId) {
        var usageBasedPricings = usageBasedPricingRepository.findByRatePlanRatePlanId(ratePlanId);
        if (!usageBasedPricings.isEmpty()) {
            log.debug("Deleting {} usage-based pricings for ratePlanId: {}", usageBasedPricings.size(), ratePlanId);
            usageBasedPricingRepository.deleteAll(usageBasedPricings);
        }
    }

    private void clearStairStepPricings(Long ratePlanId) {
        var stairStepPricings = stairStepPricingRepository.findByRatePlanRatePlanId(ratePlanId);
        if (!stairStepPricings.isEmpty()) {
            log.debug("Deleting {} stair-step pricings for ratePlanId: {}", stairStepPricings.size(), ratePlanId);
            stairStepPricingRepository.deleteAll(stairStepPricings);
        }
    }

    public enum PricingType {
        FLAT_FEE,
        TIERED_PRICING,
        VOLUME_PRICING,
        USAGE_BASED_PRICING,
        STAIR_STEP_PRICING
    }
}
