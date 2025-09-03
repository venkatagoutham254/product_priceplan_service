package aforo.productrateplanservice.tieredpricing;

import java.util.List;

public interface TieredPricingService {
    TieredPricingDTO create(Long ratePlanId, TieredPricingCreateUpdateDTO dto);
    TieredPricingDTO update(Long tieredPricingId, TieredPricingCreateUpdateDTO dto);
    List<TieredPricingDTO> getAllByRatePlanId(Long ratePlanId);
    List<TieredPricingDTO> getAll();
    TieredPricingDTO getById(Long tieredPricingId);
    void deleteById(Long tieredPricingId);
}
