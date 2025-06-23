package aforo.productrateplanservice.product.dto;

import aforo.productrateplanservice.product.enums.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductLLMTokenDTO {
    private Long productId;
    private TokenProvider tokenProvider;
    private String modelName;
    private BigDecimal tokenUnitCost;
    private CalculationMethod calculationMethod;
    private Integer quota;
    private String promptTemplate;
    private InferencePriority inferencePriority;
    private ComputeTier computeTier;
}
