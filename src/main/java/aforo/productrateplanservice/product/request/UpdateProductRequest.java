// UpdateProductRequest.java
package aforo.productrateplanservice.product.request;

import aforo.productrateplanservice.product.enums.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductRequest {
    private String productName;
    private ProductType productType;
    private String version;
    private String productDescription;
    private Map<String, Object> tags;
    private ProductCategory category;
    private Boolean visibility;
    private ProductStatus status;
    private String internalSkuCode;
    private String uom;
    private LocalDateTime effectiveStartDate;
    private LocalDateTime effectiveEndDate;
    private Boolean billable;
    private List<String> linkedRatePlans;
    private Map<String, Object> labels;
    private Long auditLogId;

    public Boolean getBillable() {
        return billable;
    }
    
    public void setBillable(Boolean billable) {
        this.billable = billable;
    }
}