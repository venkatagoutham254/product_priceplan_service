package aforo.productrateplanservice.product.entity;

import jakarta.persistence.*;
import lombok.*;
import aforo.productrateplanservice.product.enums.AuthType;
import aforo.productrateplanservice.product.enums.DBType;

@Entity
@Table(name = "aforo_product_sqlresult")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSQLResult {

    
    @Id
    private Long productId;
    
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "product_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_product_sqlresult_product_id"))
    private Product product;
    
    private String connectionString;

    @Enumerated(EnumType.STRING)
    private DBType dbType;

    @Enumerated(EnumType.STRING)
    private AuthType authType;

}
