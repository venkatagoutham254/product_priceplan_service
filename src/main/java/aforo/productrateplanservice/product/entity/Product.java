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


    private String version;

    @Column(name = "product_description", columnDefinition = "TEXT")
    private String productDescription;




    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.DRAFT;

    @Column(unique = true)
    private String internalSkuCode;


 
    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdOn;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime lastUpdated;


}