package aforo.productrateplanservice.product.repository;

import aforo.productrateplanservice.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByInternalSkuCode(String internalSkuCode);
    boolean existsByInternalSkuCodeAndOrganizationId(String internalSkuCode, Long organizationId);

    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE TRIM(LOWER(p.productName)) = TRIM(LOWER(:productName)) AND p.productId <> :productId")
    boolean existsByProductNameTrimmedIgnoreCase(@Param("productName") String productName, @Param("productId") Long productId);

    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE TRIM(LOWER(p.productName)) = TRIM(LOWER(:productName)) AND p.productId <> :productId AND p.organizationId = :organizationId")
    boolean existsByProductNameTrimmedIgnoreCaseAndOrganizationId(@Param("productName") String productName,
                                                                 @Param("productId") Long productId,
                                                                 @Param("organizationId") Long organizationId);

    Optional<Product> findByProductName(String productName);

    Optional<Product> findByProductNameIgnoreCase(String productName);
    Optional<Product> findByProductNameIgnoreCaseAndOrganizationId(String productName, Long organizationId);

    List<Product> findAllByOrganizationId(Long organizationId);
    Optional<Product> findByProductIdAndOrganizationId(Long productId, Long organizationId);
    void deleteByProductIdAndOrganizationId(Long productId, Long organizationId);
}
