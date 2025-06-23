package aforo.productrateplanservice.minimumcommitment;

import aforo.productrateplanservice.rate_plan.RatePlan;
import org.springframework.stereotype.Component;

@Component
public class MinimumCommitmentMapper {

    public MinimumCommitmentDTO toDTO(MinimumCommitment entity) {
        return MinimumCommitmentDTO.builder()
                .id(entity.getId())
                .ratePlanId(entity.getRatePlan().getRatePlanId())
                .minimumUsage(entity.getMinimumUsage())
                .minimumCharge(entity.getMinimumCharge())
                .build();
    }

    public MinimumCommitment toEntity(MinimumCommitmentCreateUpdateDTO dto, RatePlan ratePlan) {
        return MinimumCommitment.builder()
                .ratePlan(ratePlan)
                .minimumUsage(dto.getMinimumUsage())
                .minimumCharge(dto.getMinimumCharge())
                .build();
    }

    public void updateEntity(MinimumCommitment entity, MinimumCommitmentCreateUpdateDTO dto) {
        entity.setMinimumUsage(dto.getMinimumUsage());
        entity.setMinimumCharge(dto.getMinimumCharge());
    }

    public void partialUpdate(MinimumCommitment entity, MinimumCommitmentCreateUpdateDTO dto) {
        if (dto.getMinimumUsage() != null) {
            entity.setMinimumUsage(dto.getMinimumUsage());
        }
        if (dto.getMinimumCharge() != null) {
            entity.setMinimumCharge(dto.getMinimumCharge());
        }
    }
}
