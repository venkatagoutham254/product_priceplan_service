package aforo.productrateplanservice.volumepricing;

import java.util.List;

public interface VolumePricingService {
    VolumePricingDTO create(Long ratePlanId, VolumePricingCreateUpdateDTO dto);
    List<VolumePricingDTO> getByRatePlanId(Long ratePlanId);
    VolumePricingDTO updateFully(Long ratePlanId, Long id, VolumePricingCreateUpdateDTO dto); // PUT
    VolumePricingDTO updatePartially(Long ratePlanId, Long id, VolumePricingCreateUpdateDTO dto); // PATCH
    void delete(Long ratePlanId, Long id);
    void deleteByRatePlanId(Long ratePlanId);
}
