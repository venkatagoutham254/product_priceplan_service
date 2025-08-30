package aforo.productrateplanservice.product.repository;

import aforo.productrateplanservice.product.entity.ProductAPI;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductAPIRepository extends JpaRepository<ProductAPI, Long> {

    Optional<ProductAPI> findByProduct_ProductId(Long productId);
boolean existsByProduct_ProductId(Long productId);

}
