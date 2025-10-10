package aforo.productrateplanservice.product.assembler;

import aforo.productrateplanservice.product.dto.ProductDTO;
import aforo.productrateplanservice.product.entity.Product;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

/**
 * FAST assembler: no database hits, no repository calls.
 * We rely on Product.productType being set by write paths.
 * (We’ll wire that in service layers next.)
 */
@Component
@RequiredArgsConstructor
public class ProductAssembler {

    public ProductDTO toDTO(Product product) {
        return ProductDTO.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .version(product.getVersion())
                .productDescription(product.getProductDescription())
                .status(product.getStatus())
                .productType(product.getProductType()) // <- trust the entity; no DB probes
                .internalSkuCode(product.getInternalSkuCode())
                .icon(product.getIcon())
                .createdOn(product.getCreatedOn())
                .lastUpdated(product.getLastUpdated())
                .billableMetrics(null)
                .build();
    }
}
