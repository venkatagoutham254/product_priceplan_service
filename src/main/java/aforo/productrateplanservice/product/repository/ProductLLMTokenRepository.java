package aforo.productrateplanservice.product.repository;

import aforo.productrateplanservice.product.entity.ProductLLMToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface ProductLLMTokenRepository extends JpaRepository<ProductLLMToken, Long> {

    Optional<ProductLLMToken> findByProduct_ProductId(Long productId);
    boolean existsByProduct_ProductId(Long productId);

    // Organization-scoped queries
    List<ProductLLMToken> findAllByProduct_OrganizationId(Long organizationId);
    Optional<ProductLLMToken> findByProduct_ProductIdAndProduct_OrganizationId(Long productId, Long organizationId);
}
