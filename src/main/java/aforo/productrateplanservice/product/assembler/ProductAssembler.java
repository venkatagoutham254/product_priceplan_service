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
                .productType(product.getProductType())
                .version(product.getVersion())
                .productDescription(product.getProductDescription()) // updated field
                .tags(product.getTags())
                .category(product.getCategory())
                .visibility(product.isVisibility())
                .status(product.getStatus())
                .internalSkuCode(product.getInternalSkuCode())
                .uom(product.getUom())
                .effectiveStartDate(product.getEffectiveStartDate())
                .effectiveEndDate(product.getEffectiveEndDate())
                .billable(product.getBillable())
                .linkedRatePlans(product.getLinkedRatePlans())
                .labels(product.getLabels())
                .auditLogId(product.getAuditLogId())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}