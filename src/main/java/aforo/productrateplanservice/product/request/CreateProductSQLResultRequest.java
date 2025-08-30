package aforo.productrateplanservice.product.request;

import lombok.*;
import aforo.productrateplanservice.product.enums.DBType;
import aforo.productrateplanservice.product.enums.AuthType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductSQLResultRequest {

    private String connectionString;
    private DBType dbType;
    private AuthType authType;
}
