package aforo.productrateplanservice.volumepricing;

import aforo.productrateplanservice.exception.ResourceNotFoundException;
import aforo.productrateplanservice.rate_plan.RatePlan;
import aforo.productrateplanservice.rate_plan.RatePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VolumePricingServiceImpl implements VolumePricingService {

    private final VolumePricingRepository volumePricingRepository;
    private final VolumePricingMapper volumePricingMapper;
    private final RatePlanRepository ratePlanRepository;

    @Override
    @Transactional
    public VolumePricingDTO create(Long ratePlanId, VolumePricingCreateUpdateDTO dto) {
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
            .orElseThrow(() -> new ResourceNotFoundException("RatePlan not found with ID: " + ratePlanId));

        VolumePricing entity = volumePricingMapper.toEntity(dto, ratePlan);
        return volumePricingMapper.toDTO(volumePricingRepository.save(entity));
    }

    @Override
    @Transactional
    public VolumePricingDTO update(Long ratePlanId, Long volumePricingId, VolumePricingCreateUpdateDTO dto) {
        VolumePricing existing = volumePricingRepository.findById(volumePricingId)
                .orElseThrow(() -> new ResourceNotFoundException("VolumePricing not found with ID: " + volumePricingId));

        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new ResourceNotFoundException("RatePlan not found with ID: " + ratePlanId));

        existing.setRatePlan(ratePlan);
        volumePricingMapper.updateEntity(existing, dto);
        return volumePricingMapper.toDTO(volumePricingRepository.save(existing));
    }

    @Override
    public VolumePricingDTO getById(Long volumePricingId) {
        return volumePricingRepository.findById(volumePricingId)
                .map(volumePricingMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("VolumePricing not found with ID: " + volumePricingId));
    }

    @Override
    public List<VolumePricingDTO> getAll() {
        return volumePricingRepository.findAll()
                .stream()
                .map(volumePricingMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long volumePricingId) {
        if (!volumePricingRepository.existsById(volumePricingId)) {
            throw new ResourceNotFoundException("VolumePricing not found with ID: " + volumePricingId);
        }
        volumePricingRepository.deleteById(volumePricingId);
    }
}
