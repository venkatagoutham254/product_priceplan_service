package aforo.productrateplanservice.usagebasedpricing;

import java.util.List;

public interface UsageBasedPricingService {
    UsageBasedPricingDTO create(Long ratePlanId, UsageBasedPricingCreateUpdateDTO dto);
    UsageBasedPricingDTO update(Long usageBasedPricingId, UsageBasedPricingCreateUpdateDTO dto);
    List<UsageBasedPricingDTO> getAllByRatePlanId(Long ratePlanId);
    UsageBasedPricingDTO getById(Long id);
    void deleteById(Long id);
}
