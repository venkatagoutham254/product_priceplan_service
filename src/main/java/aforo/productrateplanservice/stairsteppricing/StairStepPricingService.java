package aforo.productrateplanservice.stairsteppricing;

import java.util.List;

public interface StairStepPricingService {
    StairStepPricingDTO create(Long ratePlanId, StairStepPricingCreateUpdateDTO dto);
    StairStepPricingDTO updateFully(Long ratePlanId, Long id, StairStepPricingCreateUpdateDTO dto); // PUT
    StairStepPricingDTO updatePartially(Long ratePlanId, Long id, StairStepPricingCreateUpdateDTO dto); // PATCH
    void delete(Long ratePlanId, Long id);
    List<StairStepPricingDTO> getByRatePlanId(Long ratePlanId);
    StairStepPricingDTO getById(Long ratePlanId, Long id);
}
