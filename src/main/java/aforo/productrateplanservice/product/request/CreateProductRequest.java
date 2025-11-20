package aforo.productrateplanservice.product.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    // For manual creation: productName is required
    // For import: productName is required (from external system)
    @Size(max = 255, message = "Product name must be less than 255 characters")
    private String productName;
    
    private String version;
    
    // For manual creation: internalSkuCode is required
    // For import: internalSkuCode is optional (auto-generated if not provided)
    @Size(max = 100, message = "SKU code must be less than 100 characters")
    private String internalSkuCode;
    
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String productDescription;
    
    // Source of the product: "MANUAL", "KONG", "APIGEE", etc.
    private String source;
    
    // External ID from the source system (Kong product ID, Apigee product name, etc.)
    private String externalId;
}