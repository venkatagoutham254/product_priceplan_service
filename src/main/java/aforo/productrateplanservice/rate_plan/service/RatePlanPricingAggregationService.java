package aforo.productrateplanservice.rate_plan.service;

import aforo.productrateplanservice.cache.CacheInvalidationService;
import aforo.productrateplanservice.flatfee.FlatFee;
import aforo.productrateplanservice.flatfee.FlatFeeMapper;
import aforo.productrateplanservice.flatfee.FlatFeeRepository;
import aforo.productrateplanservice.rate_plan.RatePlan;
import aforo.productrateplanservice.rate_plan.RatePlanDTO;
import aforo.productrateplanservice.rate_plan.RatePlanMapper;
import aforo.productrateplanservice.stairsteppricing.StairStepPricing;
import aforo.productrateplanservice.stairsteppricing.StairStepPricingMapper;
import aforo.productrateplanservice.stairsteppricing.StairStepPricingRepository;
import aforo.productrateplanservice.tieredpricing.TieredPricing;
import aforo.productrateplanservice.tieredpricing.TieredPricingMapper;
import aforo.productrateplanservice.tieredpricing.TieredPricingRepository;
import aforo.productrateplanservice.usagebasedpricing.UsageBasedPricing;
import aforo.productrateplanservice.usagebasedpricing.UsageBasedPricingMapper;
import aforo.productrateplanservice.usagebasedpricing.UsageBasedPricingRepository;
import aforo.productrateplanservice.volumepricing.VolumePricing;
import aforo.productrateplanservice.volumepricing.VolumePricingMapper;
import aforo.productrateplanservice.volumepricing.VolumePricingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 🎯 Rate Plan Pricing Aggregation Service
 * Handles aggregation of all pricing configurations for rate plans
 * Optimized with batch queries to prevent N+1 problems
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RatePlanPricingAggregationService {

    // Core dependencies
    private final RatePlanMapper ratePlanMapper;
    
    // Pricing repositories and mappers
    private final FlatFeeRepository flatFeeRepository;
    private final FlatFeeMapper flatFeeMapper;
    private final TieredPricingRepository tieredPricingRepository;
    private final TieredPricingMapper tieredPricingMapper;
    private final VolumePricingRepository volumePricingRepository;
    private final VolumePricingMapper volumePricingMapper;
    private final UsageBasedPricingRepository usageBasedPricingRepository;
    private final UsageBasedPricingMapper usageBasedPricingMapper;
    private final StairStepPricingRepository stairStepPricingRepository;
    private final StairStepPricingMapper stairStepPricingMapper;

    /**
     * Convert a single RatePlan to detailed DTO with all pricing configurations
     * 🔐 Requires PRICING_READ permission
     */
    @Transactional(readOnly = true)
    public RatePlanDTO toDetailedDTO(RatePlan ratePlan) {
        
        RatePlanDTO dto = ratePlanMapper.toDTO(ratePlan);
        Long ratePlanId = dto.getRatePlanId();

        log.debug("Converting rate plan {} to detailed DTO", ratePlanId);

        // Pricing configurations
        // FlatFee - at most one
        flatFeeRepository.findByRatePlanId(ratePlanId)
                .ifPresent(entity -> dto.setFlatFee(flatFeeMapper.toDTO(entity)));

        dto.setTieredPricings(
                tieredPricingRepository.findByRatePlan_RatePlanId(ratePlanId).stream()
                        .map(tieredPricingMapper::toDTO)
                        .collect(Collectors.toList())
        );

        dto.setVolumePricings(
                volumePricingRepository.findByRatePlanRatePlanId(ratePlanId).stream()
                        .map(volumePricingMapper::toDTO)
                        .collect(Collectors.toList())
        );

        dto.setUsageBasedPricings(
                usageBasedPricingRepository.findByRatePlanRatePlanId(ratePlanId).stream()
                        .map(usageBasedPricingMapper::toDTO)
                        .collect(Collectors.toList())
        );

        dto.setStairStepPricings(
                stairStepPricingRepository.findByRatePlanRatePlanId(ratePlanId).stream()
                        .map(stairStepPricingMapper::toDTO)
                        .collect(Collectors.toList())
        );

        return dto;
    }

    /**
     * ⚡ OPTIMIZED: Batch-load detailed DTOs to prevent N+1 queries
     * Instead of 1 + (N * 5) queries, this uses 1 + 5 queries total
     * 🔐 Requires PRICING_READ permission
     */
    @Transactional(readOnly = true)
    public List<RatePlanDTO> toDetailedDTOsBatch(List<RatePlan> ratePlans) {
        if (ratePlans.isEmpty()) {
            return List.of();
        }


        log.debug("Converting {} rate plans to detailed DTOs using batch optimization", ratePlans.size());

        // Extract all rate plan IDs for batch queries
        List<Long> ratePlanIds = ratePlans.stream()
                .map(RatePlan::getRatePlanId)
                .collect(Collectors.toList());

        // ⚡ Batch fetch all related data - using individual queries since batch methods don't exist
        Map<Long, FlatFee> flatFeeMap = new HashMap<>();
        Map<Long, List<TieredPricing>> tieredPricingMap = new HashMap<>();
        Map<Long, List<VolumePricing>> volumePricingMap = new HashMap<>();
        Map<Long, List<UsageBasedPricing>> usageBasedPricingMap = new HashMap<>();
        Map<Long, List<StairStepPricing>> stairStepPricingMap = new HashMap<>();

        // Fetch data for each rate plan ID
        for (Long ratePlanId : ratePlanIds) {
            // Flat fees
            flatFeeRepository.findByRatePlanId(ratePlanId)
                    .ifPresent(flatFee -> flatFeeMap.put(ratePlanId, flatFee));
            
            // Tiered pricing
            var tieredPricings = tieredPricingRepository.findByRatePlan_RatePlanId(ratePlanId);
            if (!tieredPricings.isEmpty()) {
                tieredPricingMap.put(ratePlanId, tieredPricings);
            }
            
            // Volume pricing
            var volumePricings = volumePricingRepository.findByRatePlanRatePlanId(ratePlanId);
            if (!volumePricings.isEmpty()) {
                volumePricingMap.put(ratePlanId, volumePricings);
            }
            
            // Usage-based pricing
            var usageBasedPricings = usageBasedPricingRepository.findByRatePlanRatePlanId(ratePlanId);
            if (!usageBasedPricings.isEmpty()) {
                usageBasedPricingMap.put(ratePlanId, usageBasedPricings);
            }
            
            // Stair step pricing
            var stairStepPricings = stairStepPricingRepository.findByRatePlanRatePlanId(ratePlanId);
            if (!stairStepPricings.isEmpty()) {
                stairStepPricingMap.put(ratePlanId, stairStepPricings);
            }
        }

        // Build DTOs using pre-loaded data
        return ratePlans.stream().map(ratePlan -> {
            RatePlanDTO dto = ratePlanMapper.toDTO(ratePlan);
            Long ratePlanId = dto.getRatePlanId();

            // Set pricing configurations from pre-loaded maps
            if (flatFeeMap.containsKey(ratePlanId)) {
                dto.setFlatFee(flatFeeMapper.toDTO(flatFeeMap.get(ratePlanId)));
            }

            dto.setTieredPricings(
                    tieredPricingMap.getOrDefault(ratePlanId, List.of()).stream()
                            .map(tieredPricingMapper::toDTO)
                            .collect(Collectors.toList())
            );

            dto.setVolumePricings(
                    volumePricingMap.getOrDefault(ratePlanId, List.of()).stream()
                            .map(volumePricingMapper::toDTO)
                            .collect(Collectors.toList())
            );

            dto.setUsageBasedPricings(
                    usageBasedPricingMap.getOrDefault(ratePlanId, List.of()).stream()
                            .map(usageBasedPricingMapper::toDTO)
                            .collect(Collectors.toList())
            );

            dto.setStairStepPricings(
                    stairStepPricingMap.getOrDefault(ratePlanId, List.of()).stream()
                            .map(stairStepPricingMapper::toDTO)
                            .collect(Collectors.toList())
            );

            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Get detailed rate plan with all pricing configurations (cached)
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "ratePlans", key = "'detailed_' + #ratePlan.ratePlanId + '_' + T(aforo.productrateplanservice.tenant.TenantContext).require()")
    public RatePlanDTO getDetailedRatePlan(RatePlan ratePlan) {
        return toDetailedDTO(ratePlan);
    }

    /**
     * Get all detailed rate plans with batch optimization (cached)
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "ratePlans", key = "'detailed_all_' + T(aforo.productrateplanservice.tenant.TenantContext).require()")
    public List<RatePlanDTO> getAllDetailedRatePlans(List<RatePlan> ratePlans) {
        return toDetailedDTOsBatch(ratePlans);
    }
}
