package aforo.productrateplanservice.product.repository;

import aforo.productrateplanservice.product.entity.ProductAPI;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface ProductAPIRepository extends JpaRepository<ProductAPI, Long> {

    Optional<ProductAPI> findByProduct_ProductId(Long productId);
boolean existsByProduct_ProductId(Long productId);

    // Organization-scoped queries via parent product
    List<ProductAPI> findAllByProduct_OrganizationId(Long organizationId);
    Optional<ProductAPI> findByProduct_ProductIdAndProduct_OrganizationId(Long productId, Long organizationId);

}
