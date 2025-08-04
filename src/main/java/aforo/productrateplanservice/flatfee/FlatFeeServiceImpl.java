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
    private final RatePlanRepository ratePlanRepository; // ✅ added

    @Override
    public FlatFeeDTO createFlatFee(Long ratePlanId, FlatFeeCreateUpdateDTO dto) {
        if (flatFeeRepository.existsByRatePlanId(ratePlanId)) {
            throw new IllegalStateException("FlatFee already exists for ratePlanId: " + ratePlanId);
        }

        // ✅ Validate that the ratePlanId exists
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new EntityNotFoundException("RatePlan not found with ID: " + ratePlanId));

        FlatFee entity = flatFeeMapper.toEntity(ratePlanId, dto);
        FlatFee saved = flatFeeRepository.save(entity);
        return flatFeeMapper.toDTO(saved);
    }

    @Override
    public FlatFeeDTO updateFlatFee(Long ratePlanId, FlatFeeCreateUpdateDTO dto) {
        FlatFee existing = flatFeeRepository.findByRatePlanId(ratePlanId)
                .orElseThrow(() -> new EntityNotFoundException("FlatFee config not found for ratePlanId: " + ratePlanId));

        // ✅ Update only the provided fields (partial update behavior)
        if (dto.getFlatFeeAmount() != null) {
            existing.setFlatFeeAmount(dto.getFlatFeeAmount());
        }

        if (dto.getNumberOfApiCalls() != null) {
            existing.setNumberOfApiCalls(dto.getNumberOfApiCalls());
        }

        if (dto.getOverageUnitRate() != null) {
            existing.setOverageUnitRate(dto.getOverageUnitRate());
        }

        if (dto.getGraceBuffer() != null) {
            existing.setGraceBuffer(dto.getGraceBuffer());
        }

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
}
