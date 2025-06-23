package aforo.productrateplanservice.setupfee;

import java.util.List;

public interface SetupFeeService {

    SetupFeeDTO create(Long ratePlanId, SetupFeeCreateUpdateDTO dto);

    SetupFeeDTO update(Long ratePlanId, Long id, SetupFeeCreateUpdateDTO dto);

    SetupFeeDTO getById(Long ratePlanId, Long id);

    List<SetupFeeDTO> getAllByRatePlanId(Long ratePlanId);
    SetupFeeDTO partialUpdate(Long ratePlanId, Long id, SetupFeeCreateUpdateDTO dto);
    void delete(Long ratePlanId, Long id); // ✅ updated method signature


}
