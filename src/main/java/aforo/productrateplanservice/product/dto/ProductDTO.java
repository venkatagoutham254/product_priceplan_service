package aforo.productrateplanservice.product.dto;

import lombok.*;
import aforo.productrateplanservice.client.BillableMetricResponse;
import aforo.productrateplanservice.product.enums.ProductStatus;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long productId;
    private String productName;
    private String version;
    private String productDescription;
    private ProductStatus status;
    private String internalSkuCode;
    private LocalDateTime createdOn;
    private LocalDateTime lastUpdated;

    private List<BillableMetricResponse> billableMetrics;

}