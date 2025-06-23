package aforo.productrateplanservice.product.repository;


import aforo.productrateplanservice.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByInternalSkuCode(String internalSkuCode);
    
    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE TRIM(LOWER(p.productName)) = TRIM(LOWER(:productName)) AND p.productId <> :productId")
    boolean existsByProductNameTrimmedIgnoreCase(@Param("productName") String productName, @Param("productId") Long productId);
    Optional<Product> findByProductName(String productName);

    Optional<Product> findByProductNameIgnoreCase(String productName);
}
