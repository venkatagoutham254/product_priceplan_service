package aforo.productrateplanservice.flatfee;

import aforo.productrateplanservice.rate_plan.RatePlan;
import aforo.productrateplanservice.rate_plan.RatePlanRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlatFeeServiceImpl implements FlatFeeService {

    private final FlatFeeRepository flatFeeRepository;
    private final FlatFeeMapper flatFeeMapper;
    private final RatePlanRepository ratePlanRepository; // 

    @Override
    public FlatFeeDTO createFlatFee(Long ratePlanId, FlatFeeCreateUpdateDTO dto) {
        if (flatFeeRepository.existsByRatePlanId(ratePlanId)) {
            throw new IllegalStateException("FlatFee already exists for ratePlanId: " + ratePlanId);
        }

        // 
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new EntityNotFoundException("RatePlan not found with ID: " + ratePlanId));

        FlatFee entity = flatFeeMapper.toEntity(ratePlanId, dto);
        FlatFee saved = flatFeeRepository.save(entity);
        return flatFeeMapper.toDTO(saved);
    }

    @Override
    public FlatFeeDTO updateFlatFee(Long ratePlanId, Long flatFeeId, FlatFeeCreateUpdateDTO dto) {
        // 
        ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new EntityNotFoundException("RatePlan not found with ID: " + ratePlanId));

        // 
        FlatFee existing = flatFeeRepository.findById(flatFeeId)
                .orElseThrow(() -> new EntityNotFoundException("FlatFee not found with ID: " + flatFeeId));

        // 
        existing.setRatePlanId(ratePlanId);

        // 
        flatFeeMapper.updateEntity(existing, dto);

        FlatFee updated = flatFeeRepository.save(existing);
        return flatFeeMapper.toDTO(updated);
    }

    @Override
    public FlatFeeDTO getFlatFeeByRatePlanId(Long ratePlanId) {
        FlatFee entity = flatFeeRepository.findByRatePlanId(ratePlanId)
                .orElseThrow(() -> new EntityNotFoundException("FlatFee config not found for ratePlanId: " + ratePlanId));

        return flatFeeMapper.toDTO(entity);
    }

    @Override
    public List<FlatFeeDTO> getAllFlatFees() {
        return flatFeeRepository.findAll().stream()
                .map(flatFeeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFlatFeeByRatePlanId(Long ratePlanId) {
        FlatFee entity = flatFeeRepository.findByRatePlanId(ratePlanId)
                .orElseThrow(() -> new EntityNotFoundException("FlatFee config not found for ratePlanId: " + ratePlanId));

        flatFeeRepository.delete(entity);
    }

    @Override
    public void deleteById(Long flatFeeId) {
        if (!flatFeeRepository.existsById(flatFeeId)) {
            throw new EntityNotFoundException("FlatFee not found with ID: " + flatFeeId);
        }
        flatFeeRepository.deleteById(flatFeeId);
    }
}
