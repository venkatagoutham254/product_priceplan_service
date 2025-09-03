package aforo.productrateplanservice.volumepricing;

import java.util.List;

public interface VolumePricingService {
    VolumePricingDTO create(Long ratePlanId, VolumePricingCreateUpdateDTO dto);
    VolumePricingDTO update(Long ratePlanId, Long volumePricingId, VolumePricingCreateUpdateDTO dto);
    List<VolumePricingDTO> getAllByRatePlanId(Long ratePlanId);
    List<VolumePricingDTO> getAll();
    VolumePricingDTO getById(Long id);
    void deleteById(Long id);
}
