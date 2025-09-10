package aforo.productrateplanservice.product.repository;

import aforo.productrateplanservice.product.entity.ProductStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface ProductStorageRepository extends JpaRepository<ProductStorage, Long> {

    Optional<ProductStorage> findByProduct_ProductId(Long productId);
boolean existsByProduct_ProductId(Long productId);

    // Organization-scoped queries
    List<ProductStorage> findAllByProduct_OrganizationId(Long organizationId);
    Optional<ProductStorage> findByProduct_ProductIdAndProduct_OrganizationId(Long productId, Long organizationId);
}
