package aforo.productrateplanservice.tieredpricing;

import java.util.List;

public interface TieredPricingService {
    TieredPricingDTO create(Long ratePlanId, TieredPricingCreateUpdateDTO dto);
    List<TieredPricingDTO> getByRatePlanId(Long ratePlanId);
    TieredPricingDTO updateFully(Long ratePlanId, Long id, TieredPricingCreateUpdateDTO dto); // PUT
    TieredPricingDTO updatePartially(Long ratePlanId, Long id, TieredPricingCreateUpdateDTO dto); // PATCH
    void delete(Long id);
}
