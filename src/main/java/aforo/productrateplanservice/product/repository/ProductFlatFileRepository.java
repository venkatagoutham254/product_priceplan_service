package aforo.productrateplanservice.product.repository;

import aforo.productrateplanservice.product.entity.ProductFlatFile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface ProductFlatFileRepository extends JpaRepository<ProductFlatFile, Long> {

    Optional<ProductFlatFile> findByProduct_ProductId(Long productId);
    boolean existsByProduct_ProductId(Long productId);

    // Organization-scoped queries
    List<ProductFlatFile> findAllByProduct_OrganizationId(Long organizationId);
    Optional<ProductFlatFile> findByProduct_ProductIdAndProduct_OrganizationId(Long productId, Long organizationId);
}
