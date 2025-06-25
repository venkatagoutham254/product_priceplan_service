package aforo.productrateplanservice.flatfee;

import aforo.productrateplanservice.exception.NotFoundException;
import aforo.productrateplanservice.rate_plan.RatePlan;
import aforo.productrateplanservice.rate_plan.RatePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import aforo.productrateplanservice.rate_plan.RatePlanType;


@Service
@RequiredArgsConstructor
public class FlatFeeServiceImpl implements FlatFeeService {

    private final FlatFeeRepository flatFeeRepository;
    private final RatePlanRepository ratePlanRepository;
    private final FlatFeeMapper flatFeeMapper;

    @Override
public FlatFeeDTO create(Long ratePlanId, FlatFeeCreateUpdateDTO request) {
    RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
        .orElseThrow(() -> new RuntimeException("RatePlan with ID " + ratePlanId + " not found"));

    if (ratePlan.getRatePlanType() != RatePlanType.FLATFEE) {
        throw new RuntimeException("Invalid RatePlanType. Expected FLAT_FEE but found " + ratePlan.getRatePlanType());
    }

    FlatFee flatFee = flatFeeMapper.toEntity(request);
    flatFee.setRatePlan(ratePlan);

    FlatFee saved = flatFeeRepository.save(flatFee);
    return flatFeeMapper.toDTO(saved);
}
@Override
public FlatFeeDTO updateFully(Long ratePlanId, FlatFeeCreateUpdateDTO dto) {
    FlatFee flatFee = flatFeeRepository.findByRatePlan_RatePlanId(ratePlanId)
        .orElseThrow(() -> new NotFoundException("FlatFee not found for this rate plan"));

    if (flatFee.getRatePlan().getRatePlanType() != RatePlanType.FLATFEE) {
        throw new IllegalArgumentException("Expected RatePlanType.FLATFEE but found " + flatFee.getRatePlan().getRatePlanType());
    }

    flatFee.setFlatFeeAmount(dto.getFlatFeeAmount());
    flatFee.setUsageLimit(dto.getUsageLimit());

    return flatFeeMapper.toDTO(flatFeeRepository.save(flatFee));
}

@Override
public FlatFeeDTO updatePartially(Long ratePlanId, FlatFeeCreateUpdateDTO dto) {
    FlatFee flatFee = flatFeeRepository.findByRatePlan_RatePlanId(ratePlanId)
        .orElseThrow(() -> new NotFoundException("FlatFee not found for this rate plan"));

    if (flatFee.getRatePlan().getRatePlanType() != RatePlanType.FLATFEE) {
        throw new IllegalArgumentException("Expected RatePlanType.FLATFEE but found " + flatFee.getRatePlan().getRatePlanType());
    }

    if (dto.getFlatFeeAmount() != null) flatFee.setFlatFeeAmount(dto.getFlatFeeAmount());
    if (dto.getUsageLimit() != null) flatFee.setUsageLimit(dto.getUsageLimit());

    return flatFeeMapper.toDTO(flatFeeRepository.save(flatFee));
}



    @Override
    public FlatFeeDTO getByRatePlanId(Long ratePlanId) {
        FlatFee entity = flatFeeRepository.findByRatePlan_RatePlanId(ratePlanId)
                .orElseThrow(() -> new NotFoundException("FlatFee not found"));

        return flatFeeMapper.toDTO(entity);
    }

    @Override
    public void deleteByRatePlanId(Long ratePlanId) {
        FlatFee entity = flatFeeRepository.findByRatePlan_RatePlanId(ratePlanId)
                .orElseThrow(() -> new NotFoundException("FlatFee not found"));

        flatFeeRepository.delete(entity);
    }
}
