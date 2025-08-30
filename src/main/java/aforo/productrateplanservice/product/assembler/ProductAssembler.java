package aforo.productrateplanservice.product.assembler;


import aforo.productrateplanservice.product.dto.ProductDTO;
import aforo.productrateplanservice.product.entity.Product;
import org.springframework.stereotype.Component;

// ProductAssembler.java
@Component
public class ProductAssembler {

    public ProductDTO toDTO(Product product) {
        return ProductDTO.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .version(product.getVersion())
                .productDescription(product.getProductDescription()) // updated field
                .status(product.getStatus())
                .internalSkuCode(product.getInternalSkuCode())
                .createdOn(product.getCreatedOn())
                .lastUpdated(product.getLastUpdated())
                .billableMetrics(null) // Set to null to avoid null pointer exception
                .build();
    }
}