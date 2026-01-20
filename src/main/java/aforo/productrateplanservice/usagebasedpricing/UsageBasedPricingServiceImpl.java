package aforo.productrateplanservice.usagebasedpricing;

import aforo.productrateplanservice.cache.CacheInvalidationService;
import aforo.productrateplanservice.exception.ResourceNotFoundException;
import aforo.productrateplanservice.rate_plan.RatePlan;
import aforo.productrateplanservice.rate_plan.RatePlanRepository;
import aforo.productrateplanservice.flatfee.FlatFeeRepository;
import aforo.productrateplanservice.tieredpricing.TieredPricingRepository;
import aforo.productrateplanservice.volumepricing.VolumePricingRepository;
import aforo.productrateplanservice.stairsteppricing.StairStepPricingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsageBasedPricingServiceImpl implements UsageBasedPricingService {

    private final UsageBasedPricingRepository repository;
    private final UsageBasedPricingMapper mapper;
    private final RatePlanRepository ratePlanRepository;
    private final FlatFeeRepository flatFeeRepository;
    private final TieredPricingRepository tieredPricingRepository;
    private final VolumePricingRepository volumePricingRepository;
    private final StairStepPricingRepository stairStepPricingRepository;
    private final CacheInvalidationService cacheInvalidationService;

    @Override
    @Transactional
    public UsageBasedPricingDTO create(Long ratePlanId, UsageBasedPricingCreateUpdateDTO dto) {
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new ResourceNotFoundException("RatePlan not found with ID: " + ratePlanId));

        // Auto-clear other pricing configurations if they exist
        
        // Clear flat fee
        flatFeeRepository.findByRatePlanId(ratePlanId).ifPresent(flatFeeRepository::delete);
        
        // Clear tiered pricings
        tieredPricingRepository.findByRatePlan_RatePlanId(ratePlanId)
                .forEach(tieredPricingRepository::delete);
        
        // Clear volume pricings
        volumePricingRepository.findByRatePlanRatePlanId(ratePlanId)
                .forEach(volumePricingRepository::delete);
        
        // Clear stair step pricings  
        stairStepPricingRepository.findByRatePlanRatePlanId(ratePlanId)
                .forEach(stairStepPricingRepository::delete);

        UsageBasedPricing entity = mapper.toEntity(dto, ratePlan);
        UsageBasedPricing saved = repository.save(entity);
        cacheInvalidationService.invalidateRatePlanCaches(ratePlanId);
        return mapper.toDTO(saved);
    }

    @Override
    @Transactional
    public UsageBasedPricingDTO update(Long ratePlanId, Long usageBasedPricingId, UsageBasedPricingCreateUpdateDTO dto) {
        UsageBasedPricing existing = repository.findById(usageBasedPricingId)
                .orElseThrow(() -> new ResourceNotFoundException("UsageBasedPricing not found with ID: " + usageBasedPricingId));

        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new ResourceNotFoundException("RatePlan not found with ID: " + ratePlanId));

        existing.setRatePlan(ratePlan);
        mapper.updateEntity(existing, dto);
        UsageBasedPricing saved = repository.save(existing);
        cacheInvalidationService.invalidateRatePlanCaches(ratePlanId);
        return mapper.toDTO(saved);
    }

    @Override
    public List<UsageBasedPricingDTO> getAllByRatePlanId(Long ratePlanId) {
        return repository.findByRatePlanRatePlanId(ratePlanId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<UsageBasedPricingDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public UsageBasedPricingDTO getById(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("UsageBasedPricing not found with ID: " + id));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("UsageBasedPricing not found with ID: " + id);
        }
        repository.deleteById(id);
    }
}
