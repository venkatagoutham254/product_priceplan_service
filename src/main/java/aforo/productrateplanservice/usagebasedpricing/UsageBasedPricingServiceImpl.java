package aforo.productrateplanservice.usagebasedpricing;

import aforo.productrateplanservice.rate_plan.RatePlan;
import aforo.productrateplanservice.rate_plan.RatePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import aforo.productrateplanservice.rate_plan.RatePlanType;

@Service
@RequiredArgsConstructor
public class UsageBasedPricingServiceImpl implements UsageBasedPricingService {

    private final UsageBasedPricingRepository repository;
    private final RatePlanRepository ratePlanRepository;
    private final UsageBasedPricingMapper mapper;

    @Override
public UsageBasedPricingDTO create(Long ratePlanId, UsageBasedPricingCreateUpdateDTO dto) {
    RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
            .orElseThrow(() -> new RuntimeException("RatePlan not found"));

    if (ratePlan.getRatePlanType() != RatePlanType.USAGE_BASED) {
        throw new IllegalArgumentException("Invalid RatePlanType. Expected USAGE_BASED but found " + ratePlan.getRatePlanType());
    }

    // ❗ Check if a UsageBasedPricing already exists for this RatePlan
    if (repository.findByRatePlanRatePlanId(ratePlanId).isPresent()) {
        throw new IllegalStateException("UsageBasedPricing already exists for this RatePlan. Only one is allowed.");
    }

    UsageBasedPricing entity = mapper.toEntity(ratePlan, dto);
    return mapper.toDTO(repository.save(entity));
}

    

    @Override
    public UsageBasedPricingDTO update(Long ratePlanId, Long id, UsageBasedPricingCreateUpdateDTO dto) {
        UsageBasedPricing entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("UsageBasedPricing not found"));
    
        if (!entity.getRatePlan().getRatePlanId().equals(ratePlanId)) {
            throw new IllegalArgumentException("RatePlan ID mismatch");
        }
    
        if (entity.getRatePlan().getRatePlanType() != RatePlanType.USAGE_BASED) {
            throw new IllegalArgumentException("Invalid RatePlanType. Expected USAGE_BASED but found " + entity.getRatePlan().getRatePlanType());
        }
    
        mapper.updateEntity(entity, dto);
        return mapper.toDTO(repository.save(entity));
    }
    
    @Override
    public UsageBasedPricingDTO getByRatePlanId(Long ratePlanId) {
        UsageBasedPricing entity = repository.findByRatePlanRatePlanId(ratePlanId)
                .orElseThrow(() -> new RuntimeException("UsageBasedPricing not found"));
        return mapper.toDTO(entity);
    }

    @Override
    public void delete(Long ratePlanId, Long id) {
        UsageBasedPricing entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("UsageBasedPricing not found"));

        if (!entity.getRatePlan().getRatePlanId().equals(ratePlanId)) {
            throw new IllegalArgumentException("RatePlan ID mismatch");
        }

        if (entity.getRatePlan().getRatePlanType() != RatePlanType.USAGE_BASED) {
            throw new IllegalArgumentException("Invalid RatePlanType. Expected USAGE_BASED but found " + entity.getRatePlan().getRatePlanType());
        }

        repository.delete(entity);
    }
}
