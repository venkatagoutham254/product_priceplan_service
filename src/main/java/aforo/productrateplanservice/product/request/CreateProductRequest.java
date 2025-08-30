package aforo.productrateplanservice.product.request;


import lombok.*;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {
    private String productName;
    private String version;
    private String internalSkuCode;
    private String productDescription;
        



    
    
}