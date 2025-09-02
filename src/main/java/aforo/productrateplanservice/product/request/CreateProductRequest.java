package aforo.productrateplanservice.product.request;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name must be less than 255 characters")
    private String productName;
    
    private String version;
    
    @NotBlank(message = "SKU code is required")
    @Size(max = 100, message = "SKU code must be less than 100 characters")
    private String internalSkuCode;
    
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String productDescription;
}