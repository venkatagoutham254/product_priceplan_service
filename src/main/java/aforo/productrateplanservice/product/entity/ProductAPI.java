package aforo.productrateplanservice.product.entity;

import jakarta.persistence.*;
import lombok.*;
import aforo.productrateplanservice.product.enums.AuthType;
import aforo.productrateplanservice.product.enums.LatencyClass;

@Entity
@Table(name = "aforo_product_api")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAPI {

    @Id
    private Long productId;
    
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "product_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_product_api_product_id"))
    private Product product;
    
    private String endpointUrl;

    @Enumerated(EnumType.STRING)
    private AuthType authType;

}
