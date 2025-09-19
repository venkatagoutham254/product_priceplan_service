package aforo.productrateplanservice.usagebasedpricing;

import aforo.productrateplanservice.exception.ResourceNotFoundException;
import aforo.productrateplanservice.rate_plan.RatePlan;
import aforo.productrateplanservice.rate_plan.RatePlanRepository;
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

    @Override
    @Transactional
    public UsageBasedPricingDTO create(Long ratePlanId, UsageBasedPricingCreateUpdateDTO dto) {
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new ResourceNotFoundException("RatePlan not found with ID: " + ratePlanId));

        UsageBasedPricing entity = mapper.toEntity(dto, ratePlan);
        return mapper.toDTO(repository.save(entity));
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
        return mapper.toDTO(repository.save(existing));
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
