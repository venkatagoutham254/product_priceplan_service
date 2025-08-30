package aforo.productrateplanservice.product.request;

import lombok.*;
import aforo.productrateplanservice.product.enums.AuthType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductStorageRequest {
    
    private String storageLocation;
    private AuthType authType;
}
