package aforo.productrateplanservice.product.request;

import lombok.*;
import aforo.productrateplanservice.product.enums.AuthType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductLLMTokenRequest {

    private String modelName;
    private String endpointUrl;
    private AuthType authType;

}
