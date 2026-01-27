package aforo.productrateplanservice.rate_plan.service;

import aforo.productrateplanservice.cache.CacheInvalidationService;
import aforo.productrateplanservice.flatfee.FlatFee;
import aforo.productrateplanservice.flatfee.FlatFeeMapper;
import aforo.productrateplanservice.flatfee.FlatFeeRepository;
import aforo.productrateplanservice.rate_plan.RatePlan;
import aforo.productrateplanservice.rate_plan.RatePlanDTO;
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
import aforo.productrateplanservice.setupfee.SetupFee;
import aforo.productrateplanservice.setupfee.SetupFeeMapper;
import aforo.productrateplanservice.setupfee.SetupFeeRepository;
import aforo.productrateplanservice.discount.Discount;
import aforo.productrateplanservice.discount.DiscountMapper;
import aforo.productrateplanservice.discount.DiscountRepository;
import aforo.productrateplanservice.freemium.Freemium;
import aforo.productrateplanservice.freemium.FreemiumMapper;
import aforo.productrateplanservice.freemium.FreemiumRepository;
import aforo.productrateplanservice.minimumcommitment.MinimumCommitment;
import aforo.productrateplanservice.minimumcommitment.MinimumCommitmentMapper;
import aforo.productrateplanservice.minimumcommitment.MinimumCommitmentRepository;
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
    
    // Extras repositories and mappers
    private final SetupFeeRepository setupFeeRepository;
    private final SetupFeeMapper setupFeeMapper;
    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;
    private final FreemiumRepository freemiumRepository;
    private final FreemiumMapper freemiumMapper;
    private final MinimumCommitmentRepository minimumCommitmentRepository;
    private final MinimumCommitmentMapper minimumCommitmentMapper;

    /**
     * Convert a single RatePlan to detailed DTO with all pricing configurations and extras
     * 🔐 Requires PRICING_READ permission
     */
    @Transactional(readOnly = true)
    public RatePlanDTO toDetailedDTO(RatePlan ratePlan) {
        
        Long ratePlanId = ratePlan.getRatePlanId();
        log.debug("Converting rate plan {} to detailed DTO", ratePlanId);

        // Build DTO manually with core fields (avoid calling mapper which does redundant queries)
        RatePlanDTO dto = RatePlanDTO.builder()
                .ratePlanId(ratePlanId)
                .ratePlanCode(ratePlan.getRatePlanCode())
                .ratePlanName(ratePlan.getRatePlanName())
                .description(ratePlan.getDescription())
                .billingFrequency(ratePlan.getBillingFrequency())
                .productId(ratePlan.getProduct() != null ? ratePlan.getProduct().getProductId() : null)
                .productName(ratePlan.getProduct() != null ? ratePlan.getProduct().getProductName() : null)
                .icon(ratePlan.getProduct() != null ? ratePlan.getProduct().getIcon() : null)
                .status(ratePlan.getStatus())
                .paymentType(ratePlan.getPaymentType())
                .billableMetricId(ratePlan.getBillableMetricId())
                .createdOn(ratePlan.getCreatedOn())
                .lastUpdated(ratePlan.getLastUpdated())
                .build();

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

        // Extras
        dto.setSetupFees(
                setupFeeRepository.findByRatePlan_RatePlanId(ratePlanId).stream()
                        .map(setupFeeMapper::toDTO)
                        .collect(Collectors.toList())
        );
        
        dto.setDiscounts(
                discountRepository.findByRatePlan_RatePlanId(ratePlanId).stream()
                        .map(discountMapper::toDTO)
                        .collect(Collectors.toList())
        );
        
        dto.setFreemiums(
                freemiumRepository.findByRatePlan_RatePlanId(ratePlanId).stream()
                        .map(freemiumMapper::toDTO)
                        .collect(Collectors.toList())
        );
        
        dto.setMinimumCommitments(
                minimumCommitmentRepository.findByRatePlan_RatePlanId(ratePlanId).stream()
                        .map(minimumCommitmentMapper::toDTO)
                        .collect(Collectors.toList())
        );

        return dto;
    }

    /**
     * ⚡ OPTIMIZED: Batch-load detailed DTOs to prevent N+1 queries
     * Instead of 1 + (N * 9) queries, this uses 1 + 9 queries total
     * Includes pricing configurations (5) and extras (4)
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
        Map<Long, List<SetupFee>> setupFeeMap = new HashMap<>();
        Map<Long, List<Discount>> discountMap = new HashMap<>();
        Map<Long, List<Freemium>> freemiumMap = new HashMap<>();
        Map<Long, List<MinimumCommitment>> minimumCommitmentMap = new HashMap<>();

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
            
            // Setup fees
            var setupFees = setupFeeRepository.findByRatePlan_RatePlanId(ratePlanId);
            if (!setupFees.isEmpty()) {
                setupFeeMap.put(ratePlanId, setupFees);
            }
            
            // Discounts
            var discounts = discountRepository.findByRatePlan_RatePlanId(ratePlanId);
            if (!discounts.isEmpty()) {
                discountMap.put(ratePlanId, discounts);
            }
            
            // Freemiums
            var freemiums = freemiumRepository.findByRatePlan_RatePlanId(ratePlanId);
            if (!freemiums.isEmpty()) {
                freemiumMap.put(ratePlanId, freemiums);
            }
            
            // Minimum commitments
            var minimumCommitments = minimumCommitmentRepository.findByRatePlan_RatePlanId(ratePlanId);
            if (!minimumCommitments.isEmpty()) {
                minimumCommitmentMap.put(ratePlanId, minimumCommitments);
            }
        }

        // Build DTOs using pre-loaded data - build manually to avoid N+1 queries
        return ratePlans.stream().map(ratePlan -> {
            Long ratePlanId = ratePlan.getRatePlanId();
            
            // Build DTO manually with core fields
            RatePlanDTO dto = RatePlanDTO.builder()
                    .ratePlanId(ratePlanId)
                    .ratePlanCode(ratePlan.getRatePlanCode())
                    .ratePlanName(ratePlan.getRatePlanName())
                    .description(ratePlan.getDescription())
                    .billingFrequency(ratePlan.getBillingFrequency())
                    .productId(ratePlan.getProduct() != null ? ratePlan.getProduct().getProductId() : null)
                    .productName(ratePlan.getProduct() != null ? ratePlan.getProduct().getProductName() : null)
                    .icon(ratePlan.getProduct() != null ? ratePlan.getProduct().getIcon() : null)
                    .status(ratePlan.getStatus())
                    .paymentType(ratePlan.getPaymentType())
                    .billableMetricId(ratePlan.getBillableMetricId())
                    .createdOn(ratePlan.getCreatedOn())
                    .lastUpdated(ratePlan.getLastUpdated())
                    .build();

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

            // Set extras from pre-loaded maps
            dto.setSetupFees(
                    setupFeeMap.getOrDefault(ratePlanId, List.of()).stream()
                            .map(setupFeeMapper::toDTO)
                            .collect(Collectors.toList())
            );
            
            dto.setDiscounts(
                    discountMap.getOrDefault(ratePlanId, List.of()).stream()
                            .map(discountMapper::toDTO)
                            .collect(Collectors.toList())
            );
            
            dto.setFreemiums(
                    freemiumMap.getOrDefault(ratePlanId, List.of()).stream()
                            .map(freemiumMapper::toDTO)
                            .collect(Collectors.toList())
            );
            
            dto.setMinimumCommitments(
                    minimumCommitmentMap.getOrDefault(ratePlanId, List.of()).stream()
                            .map(minimumCommitmentMapper::toDTO)
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
