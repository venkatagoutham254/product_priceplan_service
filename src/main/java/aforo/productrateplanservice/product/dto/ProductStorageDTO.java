package aforo.productrateplanservice.product.dto;

import aforo.productrateplanservice.product.enums.AuthType;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStorageDTO {

    private Long productId;
    private String storageLocation;
    private AuthType authType;
}
