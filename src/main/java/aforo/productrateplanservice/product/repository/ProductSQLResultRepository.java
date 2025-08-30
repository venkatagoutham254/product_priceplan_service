package aforo.productrateplanservice.product.repository;

import aforo.productrateplanservice.product.entity.ProductSQLResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductSQLResultRepository extends JpaRepository<ProductSQLResult, Long> {

    Optional<ProductSQLResult> findByProduct_ProductId(Long productId);
    boolean existsByProduct_ProductId(Long productId);
}
