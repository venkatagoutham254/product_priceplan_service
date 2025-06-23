package aforo.productrateplanservice.overagecharges;

import aforo.productrateplanservice.exception.NotFoundException;
import aforo.productrateplanservice.rate_plan.RatePlan;
import aforo.productrateplanservice.rate_plan.RatePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OverageChargeServiceImpl implements OverageChargeService {

    private final OverageChargeRepository repository;
    private final RatePlanRepository ratePlanRepository;
    private final OverageChargeMapper mapper;

    @Override
    public OverageChargeDTO create(Long ratePlanId, OverageChargeCreateUpdateDTO dto) {
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new NotFoundException("RatePlan not found"));
    
        // ✅ Check if an overage charge already exists for this RatePlan
        List<OverageCharge> existing = repository.findByRatePlan_RatePlanId(ratePlanId);
        if (!existing.isEmpty()) {
            throw new IllegalStateException("OverageCharge already exists for this RatePlan. Please update the existing one.");
        }
    
        OverageCharge entity = mapper.toEntity(dto, ratePlan);
        return mapper.toDTO(repository.save(entity));
    }
    

    @Override
    public OverageChargeDTO update(Long ratePlanId, Long id, OverageChargeCreateUpdateDTO dto) {
        OverageCharge existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("OverageCharge not found"));
        mapper.updateEntity(existing, dto);
        return mapper.toDTO(repository.save(existing));
    }

    @Override
    public OverageChargeDTO partialUpdate(Long ratePlanId, Long id, OverageChargeCreateUpdateDTO dto) {
        OverageCharge existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("OverageCharge not found"));
        mapper.partialUpdate(existing, dto);
        return mapper.toDTO(repository.save(existing));
    }

    @Override
    public void delete(Long ratePlanId, Long id) {
        OverageCharge existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("OverageCharge not found"));
        repository.delete(existing);
    }

    @Override
    public List<OverageChargeDTO> getAllByRatePlanId(Long ratePlanId) {
        return repository.findByRatePlan_RatePlanId(ratePlanId)
                .stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public OverageChargeDTO getById(Long ratePlanId, Long id) {
        OverageCharge entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("OverageCharge not found"));
        if (!entity.getRatePlan().getRatePlanId().equals(ratePlanId)) {
            throw new IllegalArgumentException("RatePlan ID mismatch for OverageCharge");
        }
        return mapper.toDTO(entity);
    }
}
