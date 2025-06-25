package aforo.productrateplanservice.tieredpricing;

import aforo.productrateplanservice.exception.NotFoundException;
import aforo.productrateplanservice.rate_plan.RatePlan;
import aforo.productrateplanservice.rate_plan.RatePlanRepository;
import aforo.productrateplanservice.rate_plan.RatePlanType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TieredPricingServiceImpl implements TieredPricingService {

    private final TieredPricingRepository repository;
    private final TieredPricingMapper mapper;
    private final RatePlanRepository ratePlanRepository;

    @Override
    public TieredPricingDTO create(Long ratePlanId, TieredPricingCreateUpdateDTO dto) {
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new NotFoundException("RatePlan not found"));
    
        if (ratePlan.getRatePlanType() != RatePlanType.TIERED) {
            throw new IllegalArgumentException("Invalid RatePlanType. Expected TIERED but found " + ratePlan.getRatePlanType());
        }
    
        String tierBracket = dto.getStartRange() + "-" + dto.getEndRange();
    
        TieredPricing entity = TieredPricing.builder()
                .ratePlan(ratePlan)
                .startRange(dto.getStartRange())
                .endRange(dto.getEndRange())
                .unitPrice(dto.getUnitPrice())
                .tierBracket(tierBracket)
                .build();
    
        return mapper.toDTO(repository.save(entity));
    }
    
    @Override
    public List<TieredPricingDTO> getByRatePlanId(Long ratePlanId) {
        return repository.findByRatePlan_RatePlanId(ratePlanId)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TieredPricingDTO updateFully(Long ratePlanId, Long id, TieredPricingCreateUpdateDTO dto) {
        TieredPricing pricing = repository.findByIdAndRatePlan_RatePlanId(id, ratePlanId)
            .orElseThrow(() -> new NotFoundException("Tiered pricing not found for this rate plan"));
    
        if (pricing.getRatePlan().getRatePlanType() != RatePlanType.TIERED) {
            throw new IllegalArgumentException("Invalid RatePlanType. Expected TIERED but found " + pricing.getRatePlan().getRatePlanType());
        }
    
        pricing.setStartRange(dto.getStartRange());
        pricing.setEndRange(dto.getEndRange());
        pricing.setUnitPrice(dto.getUnitPrice());
    
        return mapper.toDTO(repository.save(pricing));
    }
    
    @Override
    public TieredPricingDTO updatePartially(Long ratePlanId, Long id, TieredPricingCreateUpdateDTO dto) {
        TieredPricing pricing = repository.findByIdAndRatePlan_RatePlanId(id, ratePlanId)
            .orElseThrow(() -> new NotFoundException("Tiered pricing not found for this rate plan"));
    
        if (pricing.getRatePlan().getRatePlanType() != RatePlanType.TIERED) {
            throw new IllegalArgumentException("Invalid RatePlanType. Expected TIERED but found " + pricing.getRatePlan().getRatePlanType());
        }
    
        if (dto.getStartRange() != null) pricing.setStartRange(dto.getStartRange());
        if (dto.getEndRange() != null) pricing.setEndRange(dto.getEndRange());
        if (dto.getUnitPrice() != null) pricing.setUnitPrice(dto.getUnitPrice());
    
        return mapper.toDTO(repository.save(pricing));
    }
    

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("TieredPricing not found");
        }
        repository.deleteById(id);
    }
}
