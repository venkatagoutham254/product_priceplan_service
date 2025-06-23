package aforo.productrateplanservice.product.request;


import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import aforo.productrateplanservice.product.enums.ProductCategory;
import aforo.productrateplanservice.product.enums.ProductStatus;
import aforo.productrateplanservice.product.enums.ProductType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {
    private String productName;
    private ProductType productType;
    private String version;
    private String productDescription;
    private Map<String, Object> tags;
    private ProductCategory category;
    private boolean visibility;
    private ProductStatus status;
    private String internalSkuCode;
    private String uom;
    private LocalDateTime effectiveStartDate;
    private LocalDateTime effectiveEndDate;
    private List<String> linkedRatePlans;
    private Map<String, Object> labels;
    private Long auditLogId;

    private Boolean billable;

    
    
}