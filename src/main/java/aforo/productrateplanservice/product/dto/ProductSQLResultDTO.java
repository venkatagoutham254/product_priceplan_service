package aforo.productrateplanservice.product.dto;

import aforo.productrateplanservice.product.enums.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSQLResultDTO {
    private Long productId;
    private String connectionString;
    private DBType dbType;
    private AuthType authType;
}
