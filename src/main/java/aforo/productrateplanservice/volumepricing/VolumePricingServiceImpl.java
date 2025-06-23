package aforo.productrateplanservice.volumepricing;

import aforo.productrateplanservice.rate_plan.RatePlan;
import aforo.productrateplanservice.rate_plan.RatePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import aforo.productrateplanservice.exception.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import aforo.productrateplanservice.rate_plan.RatePlanType;

@Service
@RequiredArgsConstructor
public class VolumePricingServiceImpl implements VolumePricingService {

    private final VolumePricingRepository repository;
    private final RatePlanRepository ratePlanRepository;
    private final VolumePricingMapper mapper;

    @Override
    public VolumePricingDTO create(Long ratePlanId, VolumePricingCreateUpdateDTO dto) {
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new NotFoundException("RatePlan not found"));
    
        if (ratePlan.getRatePlanType() != RatePlanType.VOLUME_BASED) {
            throw new IllegalArgumentException("Invalid RatePlanType. Expected VOLUME_BASED but found " + ratePlan.getRatePlanType());
        }
    
        Long start = dto.getStartRange().longValue();
        Long end = dto.getEndRange().longValue();
    
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and End range cannot be null");
        }
    
        String volumeBracket = start + "-" + end;
    
        VolumePricing entity = VolumePricing.builder()
                .ratePlan(ratePlan)
                .startRange(dto.getStartRange())
                .endRange(dto.getEndRange())
                .unitPrice(dto.getUnitPrice())
                .volumeBracket(volumeBracket)
                .build();
    
        repository.save(entity);
        return mapper.toDTO(entity);
    }
    

    @Override
    public List<VolumePricingDTO> getByRatePlanId(Long ratePlanId) {
        return repository.findByRatePlan_RatePlanId(ratePlanId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
public VolumePricingDTO update(Long ratePlanId, Long id, VolumePricingCreateUpdateDTO dto) {
    VolumePricing pricing = repository.findByIdAndRatePlan_RatePlanId(id, ratePlanId)
        .orElseThrow(() -> new NotFoundException("Volume pricing not found for this rate plan"));

    if (pricing.getRatePlan().getRatePlanType() != RatePlanType.VOLUME_BASED) {
        throw new IllegalArgumentException("Invalid RatePlanType. Expected VOLUME_BASED but found " + pricing.getRatePlan().getRatePlanType());
    }

    pricing.setStartRange(dto.getStartRange());
    pricing.setEndRange(dto.getEndRange());
    pricing.setUnitPrice(dto.getUnitPrice());

    String volumeBracket = dto.getStartRange() + "-" + dto.getEndRange();
    pricing.setVolumeBracket(volumeBracket);

    repository.save(pricing);
    return mapper.toDTO(pricing);
}

@Override
public void delete(Long ratePlanId, Long id) {
    VolumePricing pricing = repository.findByIdAndRatePlan_RatePlanId(id, ratePlanId)
        .orElseThrow(() -> new NotFoundException("Volume pricing not found for this rate plan"));

    repository.delete(pricing);
}

    @Override
    public void deleteByRatePlanId(Long ratePlanId) {
        repository.deleteByRatePlan_RatePlanId(ratePlanId);
    }
}
