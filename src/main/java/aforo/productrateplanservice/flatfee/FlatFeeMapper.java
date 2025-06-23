package aforo.productrateplanservice.flatfee;

import org.springframework.stereotype.Component;
import aforo.productrateplanservice.rate_plan.RatePlan;
import aforo.productrateplanservice.flatfee.FlatFeeCreateUpdateDTO;
import aforo.productrateplanservice.flatfee.FlatFeeDTO;


@Component
public class FlatFeeMapper {

    public FlatFeeDTO toDTO(FlatFee entity) {
        return FlatFeeDTO.builder()
                .flatFeeAmount(entity.getFlatFeeAmount())
                .usageLimit(entity.getUsageLimit())
                .ratePlanId(entity.getRatePlan().getRatePlanId())
                .build();
    }

    public void updateEntity(FlatFee entity, FlatFeeCreateUpdateDTO dto) {
        entity.setFlatFeeAmount(dto.getFlatFeeAmount());
        entity.setUsageLimit(dto.getUsageLimit());
    }

    public FlatFee toEntity(FlatFeeCreateUpdateDTO dto) {
        return FlatFee.builder()
                .flatFeeAmount(dto.getFlatFeeAmount())
                .usageLimit(dto.getUsageLimit())
                .build();
    }
}
