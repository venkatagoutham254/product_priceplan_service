package aforo.productrateplanservice.discount;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DiscountCreateUpdateDTO {
    private DiscountType discountType;
    private Double percentageDiscount;
    private Double flatDiscountAmount;
    private String eligibility;
    private LocalDate startDate;
    private LocalDate endDate;
}
