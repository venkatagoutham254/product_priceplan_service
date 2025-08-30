package aforo.productrateplanservice.product.entity;

import jakarta.persistence.*;
import lombok.*;
import aforo.productrateplanservice.product.enums.TokenProvider;
import aforo.productrateplanservice.product.enums.AuthType;
import aforo.productrateplanservice.product.enums.CalculationMethod;
import aforo.productrateplanservice.product.enums.InferencePriority;
import aforo.productrateplanservice.product.enums.ComputeTier;
import java.math.BigDecimal;

@Entity
@Table(name = "aforo_product_llmtoken")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductLLMToken {

    @Id
    private Long productId;
    
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "product_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_product_llm_product_id"))
    private Product product;
    

    private String modelName;

    private String endpointUrl;

    @Enumerated(EnumType.STRING)
    private AuthType authType;
}

