package aforo.productrateplanservice.product.repository;

import aforo.productrateplanservice.product.entity.ProductFlatFile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductFlatFileRepository extends JpaRepository<ProductFlatFile, Long> {

    Optional<ProductFlatFile> findByProduct_ProductId(Long productId);
    boolean existsByProduct_ProductId(Long productId);
}
