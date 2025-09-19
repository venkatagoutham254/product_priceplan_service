package aforo.productrateplanservice.flatfee;

import java.util.List;

public interface FlatFeeService {

    FlatFeeDTO createFlatFee(Long ratePlanId, FlatFeeCreateUpdateDTO dto);

    FlatFeeDTO updateFlatFee(Long ratePlanId, Long flatFeeId, FlatFeeCreateUpdateDTO dto);

    FlatFeeDTO getFlatFeeByRatePlanId(Long ratePlanId);

    List<FlatFeeDTO> getAllFlatFees();

    void deleteFlatFeeByRatePlanId(Long ratePlanId);

    void deleteById(Long flatFeeId);
}
