package aforo.productrateplanservice.overagecharges;

import java.util.List;

public interface OverageChargeService {
    OverageChargeDTO create(Long ratePlanId, OverageChargeCreateUpdateDTO dto);
    OverageChargeDTO update(Long ratePlanId, Long id, OverageChargeCreateUpdateDTO dto);
    OverageChargeDTO partialUpdate(Long ratePlanId, Long id, OverageChargeCreateUpdateDTO dto);
    void delete(Long ratePlanId, Long id);
    List<OverageChargeDTO> getAllByRatePlanId(Long ratePlanId);
    OverageChargeDTO getById(Long ratePlanId, Long id);
}
