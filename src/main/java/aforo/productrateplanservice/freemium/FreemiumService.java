package aforo.productrateplanservice.freemium;

import java.util.List;

public interface FreemiumService {
    FreemiumDTO create(Long ratePlanId, FreemiumCreateUpdateDTO dto);
    FreemiumDTO update(Long ratePlanId, Long id, FreemiumCreateUpdateDTO dto);
    FreemiumDTO partialUpdate(Long ratePlanId, Long id, FreemiumCreateUpdateDTO dto);
    void delete(Long ratePlanId, Long id);
    FreemiumDTO getById(Long ratePlanId, Long id);
    List<FreemiumDTO> getAllByRatePlanId(Long ratePlanId);
    List<FreemiumDTO> getAll();
}
