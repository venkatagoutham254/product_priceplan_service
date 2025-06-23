package aforo.productrateplanservice.product.dto;

import aforo.productrateplanservice.product.enums.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSQLResultDTO {
    private Long productId;
    private String queryTemplate;
    private DBType dbType;
    private String resultSize;
    private Freshness freshness;
    private ExecutionFrequency executionFrequency;
    private String expectedRowRange;
    private boolean isCached;
    private JoinComplexity joinComplexity;
}
