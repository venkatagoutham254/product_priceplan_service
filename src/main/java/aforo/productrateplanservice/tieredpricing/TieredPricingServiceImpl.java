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
public TieredPricingDTO update(Long ratePlanId, Long id, TieredPricingCreateUpdateDTO dto) {
    TieredPricing entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("TieredPricing not found"));

    if (!entity.getRatePlan().getRatePlanId().equals(ratePlanId)) {
        throw new IllegalArgumentException("RatePlan ID mismatch");
    }

    if (entity.getRatePlan().getRatePlanType() != RatePlanType.TIERED) {
        throw new IllegalArgumentException("Invalid RatePlanType. Expected TIERED but found " + entity.getRatePlan().getRatePlanType());
    }

    String tierBracket = dto.getStartRange() + "-" + dto.getEndRange();

    entity.setStartRange(dto.getStartRange());
    entity.setEndRange(dto.getEndRange());
    entity.setUnitPrice(dto.getUnitPrice());
    entity.setTierBracket(tierBracket);

    return mapper.toDTO(repository.save(entity));
}


    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("TieredPricing not found");
        }
        repository.deleteById(id);
    }
}
