package aforo.productrateplanservice.usagebasedpricing;

import java.util.Optional;

public interface UsageBasedPricingService {
    UsageBasedPricingDTO create(Long ratePlanId, UsageBasedPricingCreateUpdateDTO dto);
    UsageBasedPricingDTO update(Long ratePlanId, Long id, UsageBasedPricingCreateUpdateDTO dto);
    UsageBasedPricingDTO getByRatePlanId(Long ratePlanId);
    void delete(Long ratePlanId, Long id);
}
