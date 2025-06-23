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
public FlatFeeDTO update(Long ratePlanId, FlatFeeCreateUpdateDTO request) {
    RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
        .orElseThrow(() -> new RuntimeException("RatePlan with ID " + ratePlanId + " not found"));

    if (ratePlan.getRatePlanType() != RatePlanType.FLATFEE) {
        throw new RuntimeException("Invalid RatePlanType. Expected FLAT_FEE but found " + ratePlan.getRatePlanType());
    }

    FlatFee flatFee = flatFeeRepository.findByRatePlan_RatePlanId(ratePlanId)
        .orElseThrow(() -> new RuntimeException("FlatFee not found for ratePlanId: " + ratePlanId));

    // Apply new values from the request
    flatFee.setFlatFeeAmount(request.getFlatFeeAmount());
    flatFee.setUsageLimit(request.getUsageLimit());

    FlatFee saved = flatFeeRepository.save(flatFee);
    return flatFeeMapper.toDTO(saved);
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
