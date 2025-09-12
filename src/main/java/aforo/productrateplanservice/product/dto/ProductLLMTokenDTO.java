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
    private String modelName;
    private String endpointUrl;
    private AuthType authType;
}
