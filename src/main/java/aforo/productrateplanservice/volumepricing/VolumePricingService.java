package aforo.productrateplanservice.volumepricing;

import java.util.List;

public interface VolumePricingService {

    VolumePricingDTO create(Long ratePlanId, VolumePricingCreateUpdateDTO dto);

    VolumePricingDTO update(Long ratePlanId, Long volumePricingId, VolumePricingCreateUpdateDTO dto);

    VolumePricingDTO getById(Long volumePricingId);

    List<VolumePricingDTO> getAll();

    void delete(Long volumePricingId);
}
