package aforo.productrateplanservice.product.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Null;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.UpdateTimestamp;

import aforo.productrateplanservice.product.enums.ProductCategory;
import aforo.productrateplanservice.product.enums.ProductStatus;    
import aforo.productrateplanservice.product.enums.ProductType;  
import aforo.productrateplanservice.product.util.JsonMapConverter;
import aforo.productrateplanservice.product.util.JsonListConverter;
// Product.java
@Entity
@Table(
    name = "aforo_product",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_aforo_product__org_name", columnNames = {"organization_id", "product_name"}),
        @UniqueConstraint(name = "uq_aforo_product__org_sku", columnNames = {"organization_id", "internal_sku_code"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    private String version;

    @Column(name = "product_description", columnDefinition = "TEXT")
    private String productDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = true)
    private ProductType productType;

    @Column(name = "internal_sku_code")
    private String internalSkuCode;

    @Column(name = "icon", nullable = true, length = 1024)
    private String icon;

    @Column(name = "source", nullable = false)
    @Builder.Default
    private String source = "MANUAL";

    @Column(name = "external_id", nullable = true)
    private String externalId;

    @Column(nullable = false)
    private LocalDateTime createdOn;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime lastUpdated;

}