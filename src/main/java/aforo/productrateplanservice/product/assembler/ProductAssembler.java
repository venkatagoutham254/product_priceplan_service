package aforo.productrateplanservice.product.assembler;

import aforo.productrateplanservice.product.dto.ProductDTO;
import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.enums.ProductType;
import aforo.productrateplanservice.product.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// ProductAssembler.java
@Component
@RequiredArgsConstructor
public class ProductAssembler {

    private final ProductAPIRepository productAPIRepository;
    private final ProductFlatFileRepository productFlatFileRepository;
    private final ProductSQLResultRepository productSQLResultRepository;
    private final ProductLLMTokenRepository productLLMTokenRepository;
    private final ProductStorageRepository productStorageRepository;

    public ProductDTO toDTO(Product product) {
        ProductType determinedType = determineProductType(product);
        
        return ProductDTO.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .version(product.getVersion())
                .productDescription(product.getProductDescription())
                .status(product.getStatus())
                .productType(determinedType)
                .internalSkuCode(product.getInternalSkuCode())
                .icon(product.getIcon())
                .createdOn(product.getCreatedOn())
                .lastUpdated(product.getLastUpdated())
                .billableMetrics(null) // Set to null to avoid null pointer exception
                .build();
    }

    private ProductType determineProductType(Product product) {
        // Return stored productType if available
        if (product.getProductType() != null) {
            return product.getProductType();
        }

        // Determine productType based on which child table has data
        Long productId = product.getProductId();
        if (productId == null) {
            return null; // For new products
        }

        if (productAPIRepository.existsById(productId)) {
            return ProductType.API;
        }
        if (productFlatFileRepository.existsById(productId)) {
            return ProductType.FlatFile;
        }
        if (productSQLResultRepository.existsById(productId)) {
            return ProductType.SQLResult;
        }
        if (productLLMTokenRepository.existsById(productId)) {
            return ProductType.LLMToken;
        }
        if (productStorageRepository.existsById(productId)) {
            return ProductType.Storage;
        }

        return null; // No type configuration found
    }
}