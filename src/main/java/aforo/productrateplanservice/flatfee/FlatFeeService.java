package aforo.productrateplanservice.flatfee;

public interface FlatFeeService {
    FlatFeeDTO create(Long ratePlanId, FlatFeeCreateUpdateDTO dto);
    FlatFeeDTO updateFully(Long ratePlanId, FlatFeeCreateUpdateDTO dto);  // PUT
    FlatFeeDTO updatePartially(Long ratePlanId, FlatFeeCreateUpdateDTO dto);  // PATCH
    FlatFeeDTO getByRatePlanId(Long ratePlanId);
    void deleteByRatePlanId(Long ratePlanId);
}
