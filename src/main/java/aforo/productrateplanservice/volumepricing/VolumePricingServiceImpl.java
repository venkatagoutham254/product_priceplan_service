package aforo.productrateplanservice.volumepricing;

import aforo.productrateplanservice.exception.ResourceNotFoundException;
import aforo.productrateplanservice.rate_plan.RatePlan;
import aforo.productrateplanservice.rate_plan.RatePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VolumePricingServiceImpl implements VolumePricingService {

    private final VolumePricingRepository repository;
    private final VolumePricingMapper mapper;
    private final RatePlanRepository ratePlanRepository;

    @Override
    public VolumePricingDTO create(Long ratePlanId, VolumePricingCreateUpdateDTO dto) {
        // ✅ Validate RatePlan
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new ResourceNotFoundException("RatePlan not found with ID: " + ratePlanId));

        // ✅ Map DTO → Entity (parent + tiers if included)
        VolumePricing entity = mapper.toEntity(dto, ratePlan);

        // ✅ Save parent with cascade (tiers saved automatically)
        VolumePricing saved = repository.save(entity);

        return mapper.toDTO(saved);
    }

    @Override
    public VolumePricingDTO update(Long ratePlanId, Long volumePricingId, VolumePricingCreateUpdateDTO dto) {
        // ✅ Validate existing VolumePricing
        VolumePricing existing = repository.findById(volumePricingId)
                .orElseThrow(() -> new ResourceNotFoundException("VolumePricing not found with ID: " + volumePricingId));

        // ✅ Validate RatePlan
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new ResourceNotFoundException("RatePlan not found with ID: " + ratePlanId));

        // ✅ Reassign RatePlan
        existing.setRatePlan(ratePlan);

        // ✅ Update fields + tiers
        mapper.updateEntity(existing, dto);

        // ✅ Save updated entity
        VolumePricing updated = repository.save(existing);

        return mapper.toDTO(updated);
    }

    @Override
    public List<VolumePricingDTO> getAllByRatePlanId(Long ratePlanId) {
        return repository.findByRatePlanRatePlanId(ratePlanId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public VolumePricingDTO getById(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("VolumePricing not found with ID: " + id));
    }

    @Override
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("VolumePricing not found with ID: " + id);
        }
        repository.deleteById(id);
    }
}
