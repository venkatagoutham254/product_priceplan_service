package aforo.productrateplanservice.tieredpricing;

import java.util.List;

public interface TieredPricingService {
    TieredPricingDTO create(Long ratePlanId, TieredPricingCreateUpdateDTO dto);
    List<TieredPricingDTO> getByRatePlanId(Long ratePlanId);
    TieredPricingDTO update(Long ratePlanId, Long id, TieredPricingCreateUpdateDTO dto);
    void delete(Long id);
}
