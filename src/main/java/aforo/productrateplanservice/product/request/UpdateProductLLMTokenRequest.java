package aforo.productrateplanservice.product.request;

import aforo.productrateplanservice.product.enums.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductLLMTokenRequest {
 
    private String modelName;
    private String endpointUrl;
    private AuthType authType;
}
