package aforo.productrateplanservice.product.request;

import lombok.*;
import aforo.productrateplanservice.product.enums.TokenProvider;
import aforo.productrateplanservice.product.enums.CalculationMethod;
import aforo.productrateplanservice.product.enums.InferencePriority;
import aforo.productrateplanservice.product.enums.ComputeTier;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductLLMTokenRequest {
    private TokenProvider tokenProvider;
    private String modelName;
    private BigDecimal tokenUnitCost;
    private CalculationMethod calculationMethod;
    private Integer quota;
    private String promptTemplate;
    private InferencePriority inferencePriority;
    private ComputeTier computeTier;
}
