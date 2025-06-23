package aforo.productrateplanservice.flatfee;

public interface FlatFeeService {
    FlatFeeDTO create(Long ratePlanId, FlatFeeCreateUpdateDTO dto);
    FlatFeeDTO update(Long ratePlanId, FlatFeeCreateUpdateDTO dto);
    FlatFeeDTO getByRatePlanId(Long ratePlanId);
    void deleteByRatePlanId(Long ratePlanId);
}
