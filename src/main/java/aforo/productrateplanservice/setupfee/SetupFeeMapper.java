package aforo.productrateplanservice.setupfee;

import aforo.productrateplanservice.rate_plan.RatePlan;
import org.springframework.stereotype.Component;

@Component
public class SetupFeeMapper {

    public SetupFeeDTO toDTO(SetupFee entity) {
        return SetupFeeDTO.builder()
                .id(entity.getId())
                .ratePlanId(entity.getRatePlan().getRatePlanId())
                .setupFee(entity.getSetupFee())
                .applicationTiming(entity.getApplicationTiming())
                .invoiceDescription(entity.getInvoiceDescription())
                .build();
    }

    public SetupFee toEntity(SetupFeeCreateUpdateDTO dto, RatePlan ratePlan) {
        return SetupFee.builder()
                .ratePlan(ratePlan)
                .setupFee(dto.getSetupFee())
                .applicationTiming(dto.getApplicationTiming())
                .invoiceDescription(dto.getInvoiceDescription())
                .build();
    }

    public void updateEntity(SetupFee entity, SetupFeeCreateUpdateDTO dto) {
        entity.setSetupFee(dto.getSetupFee());
        entity.setApplicationTiming(dto.getApplicationTiming());
        entity.setInvoiceDescription(dto.getInvoiceDescription());
    }

    public void partialUpdate(SetupFee entity, SetupFeeCreateUpdateDTO dto) {
        if (dto.getSetupFee() != null) {
            entity.setSetupFee(dto.getSetupFee());
        }
        if (dto.getApplicationTiming() != null) {
            entity.setApplicationTiming(dto.getApplicationTiming());
        }
        if (dto.getInvoiceDescription() != null) {
            entity.setInvoiceDescription(dto.getInvoiceDescription());
        }
    }
}
