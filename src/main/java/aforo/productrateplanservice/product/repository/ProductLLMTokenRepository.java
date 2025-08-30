package aforo.productrateplanservice.product.repository;

import aforo.productrateplanservice.product.entity.ProductLLMToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductLLMTokenRepository extends JpaRepository<ProductLLMToken, Long> {

    Optional<ProductLLMToken> findByProduct_ProductId(Long productId);
    boolean existsByProduct_ProductId(Long productId);
}
