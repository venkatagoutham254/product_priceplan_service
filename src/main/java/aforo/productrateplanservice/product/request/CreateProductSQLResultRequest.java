package aforo.productrateplanservice.product.request;

import lombok.*;
import aforo.productrateplanservice.product.enums.DBType;
import aforo.productrateplanservice.product.enums.Freshness;
import aforo.productrateplanservice.product.enums.ExecutionFrequency;
import aforo.productrateplanservice.product.enums.JoinComplexity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductSQLResultRequest {
    private String queryTemplate;
    private DBType dbType;
    private String resultSize;
    private Freshness freshness;
    private ExecutionFrequency executionFrequency;
    private String expectedRowRange;
    private boolean isCached;
    private JoinComplexity joinComplexity;
}
