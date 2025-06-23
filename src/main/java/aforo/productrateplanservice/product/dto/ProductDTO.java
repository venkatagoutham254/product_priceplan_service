package aforo.productrateplanservice.product.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import aforo.productrateplanservice.product.enums.ProductCategory;
import aforo.productrateplanservice.product.enums.ProductStatus;
import aforo.productrateplanservice.product.enums.ProductType;
// ProductDTO.java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long productId;
    private String productName;
    private ProductType productType;
    private String version;
    private String productDescription; // Changed field name
    private Map<String, Object> tags;
    private ProductCategory category;
    private boolean visibility;
    private ProductStatus status;
    private String internalSkuCode;
    private String uom;
    private LocalDateTime effectiveStartDate;
    private LocalDateTime effectiveEndDate;
    private Boolean billable;
    private List<String> linkedRatePlans;
    private Map<String, Object> labels;
    private Long auditLogId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}