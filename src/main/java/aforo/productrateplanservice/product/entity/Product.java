package aforo.productrateplanservice.product.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Null;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.CreationTimestamp;
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
        @UniqueConstraint(name = "uc_product_name_trimmed", columnNames = {"product_name"})
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

    @Column(name = "product_name", unique = true)
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

    @Column(unique = true)
    private String internalSkuCode;

    @Column(name = "icon", nullable = true, length = 1024)
    private String icon;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdOn;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime lastUpdated;

}