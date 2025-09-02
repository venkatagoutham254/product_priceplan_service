package aforo.productrateplanservice.product.dto;

import lombok.*;
import aforo.productrateplanservice.client.BillableMetricResponse;
import aforo.productrateplanservice.product.enums.ProductStatus;
import aforo.productrateplanservice.product.enums.ProductType;
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
    private ProductType productType;
    private String internalSkuCode;
    private String icon;
    private LocalDateTime createdOn;
    private LocalDateTime lastUpdated;

    private List<BillableMetricResponse> billableMetrics;

}