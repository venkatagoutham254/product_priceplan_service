package aforo.productrateplanservice.volumepricing;

import java.util.List;

public interface VolumePricingService {
    VolumePricingDTO create(Long ratePlanId, VolumePricingCreateUpdateDTO dto);
    List<VolumePricingDTO> getByRatePlanId(Long ratePlanId);
    VolumePricingDTO update(Long ratePlanId, Long id, VolumePricingCreateUpdateDTO dto);
    void delete(Long ratePlanId, Long id);
    
    void deleteByRatePlanId(Long ratePlanId);
}
