package aforo.productrateplanservice.product.repository;

import aforo.productrateplanservice.product.entity.ProductSQLResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface ProductSQLResultRepository extends JpaRepository<ProductSQLResult, Long> {

    Optional<ProductSQLResult> findByProduct_ProductId(Long productId);
    boolean existsByProduct_ProductId(Long productId);

    // Organization-scoped queries
    List<ProductSQLResult> findAllByProduct_OrganizationId(Long organizationId);
    Optional<ProductSQLResult> findByProduct_ProductIdAndProduct_OrganizationId(Long productId, Long organizationId);
}
