package aforo.productrateplanservice.product.request;

import aforo.productrateplanservice.product.enums.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductSQLResultRequest {
    private String connectionString;
    private DBType dbType;
    private AuthType authType;
}
