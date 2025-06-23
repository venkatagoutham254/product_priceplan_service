package aforo.productrateplanservice.discount;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DiscountDTO {
    private Long id;
    private Long ratePlanId;
    private DiscountType discountType;
    private Double percentageDiscount;
    private Double flatDiscountAmount;
    private String eligibility;
    private LocalDate startDate;
    private LocalDate endDate;
}
