package aforo.productrateplanservice.rate_plan;

import java.util.List;

public interface RatePlanService {

    RatePlanDTO createRatePlan(CreateRatePlanRequest request);

    List<RatePlanDTO> getAllRatePlans();

    List<RatePlanDTO> getRatePlansByProductId(Long productId);

    RatePlanDTO getRatePlanById(Long ratePlanId);

    void deleteRatePlan(Long ratePlanId);

    RatePlanDTO updateRatePlanFully(Long ratePlanId, UpdateRatePlanRequest request);
    RatePlanDTO updateRatePlanPartially(Long ratePlanId, UpdateRatePlanRequest request);
    
}
