package aforo.productrateplanservice.discount;

import aforo.productrateplanservice.rate_plan.RatePlan;
import org.springframework.stereotype.Component;

@Component
public class DiscountMapper {

    public DiscountDTO toDTO(Discount entity) {
        return DiscountDTO.builder()
                .id(entity.getId())
                .ratePlanId(entity.getRatePlan().getRatePlanId())
                .discountType(entity.getDiscountType())
                .percentageDiscount(entity.getPercentageDiscount())
                .flatDiscountAmount(entity.getFlatDiscountAmount())
                .eligibility(entity.getEligibility())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .build();
    }

    public Discount toEntity(DiscountCreateUpdateDTO dto, RatePlan ratePlan) {
        return Discount.builder()
                .ratePlan(ratePlan)
                .discountType(dto.getDiscountType())
                .percentageDiscount(dto.getPercentageDiscount())
                .flatDiscountAmount(dto.getFlatDiscountAmount())
                .eligibility(dto.getEligibility())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();
    }

    public void updateEntity(Discount entity, DiscountCreateUpdateDTO dto) {
        entity.setDiscountType(dto.getDiscountType());
        entity.setPercentageDiscount(dto.getPercentageDiscount());
        entity.setFlatDiscountAmount(dto.getFlatDiscountAmount());
        entity.setEligibility(dto.getEligibility());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
    }

    public void partialUpdate(Discount entity, DiscountCreateUpdateDTO dto) {
        if (dto.getDiscountType() != null) entity.setDiscountType(dto.getDiscountType());
        if (dto.getPercentageDiscount() != null) entity.setPercentageDiscount(dto.getPercentageDiscount());
        if (dto.getFlatDiscountAmount() != null) entity.setFlatDiscountAmount(dto.getFlatDiscountAmount());
        if (dto.getEligibility() != null) entity.setEligibility(dto.getEligibility());
        if (dto.getStartDate() != null) entity.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) entity.setEndDate(dto.getEndDate());
    }
}
