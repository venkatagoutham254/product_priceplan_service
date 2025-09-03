package aforo.productrateplanservice.stairsteppricing;

import aforo.productrateplanservice.exception.ResourceNotFoundException;
import aforo.productrateplanservice.rate_plan.RatePlan;
import aforo.productrateplanservice.rate_plan.RatePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StairStepPricingServiceImpl implements StairStepPricingService {

    private final StairStepPricingRepository repository;
    private final StairStepPricingMapper mapper;
    private final RatePlanRepository ratePlanRepository;

    @Override
    public StairStepPricingDTO create(Long ratePlanId, StairStepPricingCreateUpdateDTO dto) {
        // ✅ Validate RatePlan
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new ResourceNotFoundException("RatePlan not found with ID: " + ratePlanId));

        // ✅ Map DTO → Entity (parent + tiers)
        StairStepPricing entity = mapper.toEntity(dto, ratePlan);

        // ✅ Save parent with cascade (tiers saved automatically)
        StairStepPricing saved = repository.save(entity);

        return mapper.toDTO(saved);
    }

    @Override
    public StairStepPricingDTO update(Long ratePlanId, Long stairStepPricingId, StairStepPricingCreateUpdateDTO dto) {
        // ✅ Validate existing StairStepPricing
        StairStepPricing existing = repository.findById(stairStepPricingId)
                .orElseThrow(() -> new ResourceNotFoundException("StairStepPricing not found with ID: " + stairStepPricingId));

        // ✅ Validate RatePlan
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new ResourceNotFoundException("RatePlan not found with ID: " + ratePlanId));

        // ✅ Reassign RatePlan
        existing.setRatePlan(ratePlan);

        // ✅ Update tiers + optional fields
        mapper.updateEntity(existing, dto);

        // ✅ Save updated entity
        StairStepPricing updated = repository.save(existing);

        return mapper.toDTO(updated);
    }

    @Override
    public List<StairStepPricingDTO> getAllByRatePlanId(Long ratePlanId) {
        return repository.findByRatePlanRatePlanId(ratePlanId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<StairStepPricingDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public StairStepPricingDTO getById(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("StairStepPricing not found with ID: " + id));
    }

    @Override
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("StairStepPricing not found with ID: " + id);
        }
        repository.deleteById(id);
    }
}
