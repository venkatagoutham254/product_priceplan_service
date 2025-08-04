package aforo.productrateplanservice.stairsteppricing;

import java.util.List;

public interface StairStepPricingService {
    StairStepPricingDTO create(Long ratePlanId, StairStepPricingCreateUpdateDTO dto);
    StairStepPricingDTO update(Long ratePlanId, Long stairStepPricingId, StairStepPricingCreateUpdateDTO dto);
    List<StairStepPricingDTO> getAllByRatePlanId(Long ratePlanId);
    StairStepPricingDTO getById(Long id);
    void deleteById(Long id);
}
