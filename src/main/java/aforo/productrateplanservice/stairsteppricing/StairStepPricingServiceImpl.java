package aforo.productrateplanservice.stairsteppricing;

import aforo.productrateplanservice.rate_plan.RatePlan;
import aforo.productrateplanservice.rate_plan.RatePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import aforo.productrateplanservice.rate_plan.RatePlanType;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StairStepPricingServiceImpl implements StairStepPricingService {

    private final StairStepPricingRepository repository;
    private final RatePlanRepository ratePlanRepository;
    private final StairStepPricingMapper mapper;

    @Override
public StairStepPricingDTO create(Long ratePlanId, StairStepPricingCreateUpdateDTO dto) {
    RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
            .orElseThrow(() -> new IllegalArgumentException("RatePlan not found"));

    if (ratePlan.getRatePlanType() != RatePlanType.STAIRSTEP) {
        throw new IllegalArgumentException("Invalid RatePlanType. Expected STAIR_STEP but found " + ratePlan.getRatePlanType());
    }

    String bracket = dto.getUsageThresholdStart() + "-" +
            (dto.getUsageThresholdEnd() == null ? "Unlimited" : dto.getUsageThresholdEnd());

    StairStepPricing entity = StairStepPricing.builder()
            .ratePlan(ratePlan)
            .usageThresholdStart(dto.getUsageThresholdStart())
            .usageThresholdEnd(dto.getUsageThresholdEnd())
            .monthlyCharge(dto.getMonthlyCharge())
            .stairBracket(bracket)
            .build();

    return mapper.toDTO(repository.save(entity));
}


@Override
public StairStepPricingDTO update(Long ratePlanId, Long id, StairStepPricingCreateUpdateDTO dto) {
    StairStepPricing entity = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Stair Step not found"));

    if (!entity.getRatePlan().getRatePlanId().equals(ratePlanId)) {
        throw new IllegalArgumentException("RatePlan ID mismatch");
    }

    if (entity.getRatePlan().getRatePlanType() != RatePlanType.STAIRSTEP) {
        throw new IllegalArgumentException("Invalid RatePlanType. Expected STAIR_STEP but found " + entity.getRatePlan().getRatePlanType());
    }

    String bracket = dto.getUsageThresholdStart() + "-" +
            (dto.getUsageThresholdEnd() == null ? "Unlimited" : dto.getUsageThresholdEnd());

    entity.setUsageThresholdStart(dto.getUsageThresholdStart());
    entity.setUsageThresholdEnd(dto.getUsageThresholdEnd());
    entity.setMonthlyCharge(dto.getMonthlyCharge());
    entity.setStairBracket(bracket);

    return mapper.toDTO(repository.save(entity));
}


    @Override
    public void delete(Long ratePlanId, Long id) {
        StairStepPricing entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stair Step not found"));

        if (!entity.getRatePlan().getRatePlanId().equals(ratePlanId)) {
            throw new IllegalArgumentException("RatePlan ID mismatch");
        }

        repository.delete(entity);
    }

    @Override
    public List<StairStepPricingDTO> getByRatePlanId(Long ratePlanId) {
        return repository.findByRatePlanRatePlanId(ratePlanId)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StairStepPricingDTO getById(Long ratePlanId, Long id) {
        StairStepPricing entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stair Step not found"));

        if (!entity.getRatePlan().getRatePlanId().equals(ratePlanId)) {
            throw new IllegalArgumentException("RatePlan ID mismatch");
        }

        return mapper.toDTO(entity);
    }
}
