package aforo.productrateplanservice.flatfee;

import org.springframework.stereotype.Component;

@Component
public class FlatFeeMapper {

    public FlatFee toEntity(Long ratePlanId, FlatFeeCreateUpdateDTO dto) {
        return FlatFee.builder()
                .ratePlanId(ratePlanId)
                .flatFeeAmount(dto.getFlatFeeAmount())
                .numberOfApiCalls(dto.getNumberOfApiCalls())
                .overageUnitRate(dto.getOverageUnitRate())      // 
                .graceBuffer(dto.getGraceBuffer())              // 
                .build();
    }

    public void updateEntity(FlatFee entity, FlatFeeCreateUpdateDTO dto) {
        if (dto.getFlatFeeAmount() != null) {
            entity.setFlatFeeAmount(dto.getFlatFeeAmount());
        }
        if (dto.getNumberOfApiCalls() != null) {
            entity.setNumberOfApiCalls(dto.getNumberOfApiCalls());
        }

        if (dto.getOverageUnitRate() != null) {
            entity.setOverageUnitRate(dto.getOverageUnitRate()); // 
        }
        if (dto.getGraceBuffer() != null) {
            entity.setGraceBuffer(dto.getGraceBuffer());         // 
        }
    }

    public FlatFeeDTO toDTO(FlatFee entity) {
        return FlatFeeDTO.builder()
                .flatFeeId(entity.getFlatFeeId())
                .ratePlanId(entity.getRatePlanId())
                .flatFeeAmount(entity.getFlatFeeAmount())
                .numberOfApiCalls(entity.getNumberOfApiCalls())
                .overageUnitRate(entity.getOverageUnitRate())   // ✅ new
                .graceBuffer(entity.getGraceBuffer())           // ✅ new
                .ratePlanType(entity.getRatePlanType())
                .build();
    }
}
