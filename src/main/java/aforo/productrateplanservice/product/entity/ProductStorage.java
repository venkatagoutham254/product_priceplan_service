package aforo.productrateplanservice.product.entity;

import jakarta.persistence.*;
import lombok.*;
import aforo.productrateplanservice.product.enums.AuthType;

@Entity
@Table(name = "aforo_product_storage")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStorage{

    @Id
    private Long productId;
    
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "product_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_product_storage_product_id"))
    private Product product;
    
    private String storageLocation;

    @Enumerated(EnumType.STRING)
    private AuthType authType;

}
