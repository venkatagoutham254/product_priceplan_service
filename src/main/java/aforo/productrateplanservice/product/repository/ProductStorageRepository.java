package aforo.productrateplanservice.product.repository;

import aforo.productrateplanservice.product.entity.ProductStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductStorageRepository extends JpaRepository<ProductStorage, Long> {

    Optional<ProductStorage> findByProduct_ProductId(Long productId);
boolean existsByProduct_ProductId(Long productId);

}
