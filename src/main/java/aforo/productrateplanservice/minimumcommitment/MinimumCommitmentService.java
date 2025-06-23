package aforo.productrateplanservice.minimumcommitment;

import java.util.List;

public interface MinimumCommitmentService {
    MinimumCommitmentDTO create(Long ratePlanId, MinimumCommitmentCreateUpdateDTO dto);
    List<MinimumCommitmentDTO> getAllByRatePlanId(Long ratePlanId);
    MinimumCommitmentDTO getById(Long ratePlanId, Long id);
    MinimumCommitmentDTO update(Long ratePlanId, Long id, MinimumCommitmentCreateUpdateDTO dto);
    MinimumCommitmentDTO partialUpdate(Long ratePlanId, Long id, MinimumCommitmentCreateUpdateDTO dto);
    void delete(Long ratePlanId, Long id);
}
