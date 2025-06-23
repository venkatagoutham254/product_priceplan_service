package aforo.productrateplanservice.product.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

    @Column(name = "product_name", nullable = false, unique = true)
    private String productName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType productType;

    private String version;

    @Column(name = "product_description", columnDefinition = "TEXT")
    private String productDescription;

    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> tags;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @Column(nullable = false, unique = true)
    private String internalSkuCode;

    private String uom;

    @Column(nullable = false)
    private LocalDateTime effectiveStartDate;

    private LocalDateTime effectiveEndDate;


    @Convert(converter = JsonListConverter.class)
    private List<String> linkedRatePlans;

    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> labels;

    private Long auditLogId;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    @Column(name = "visibility", nullable = false)
    private boolean visibility;
    
    @Column(name = "billable", nullable = false)
    private Boolean billable;
    

}