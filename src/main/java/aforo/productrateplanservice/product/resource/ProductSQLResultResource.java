package aforo.productrateplanservice.product.resource;

import aforo.productrateplanservice.product.dto.ProductSQLResultDTO;
import aforo.productrateplanservice.product.request.CreateProductSQLResultRequest;
import aforo.productrateplanservice.product.request.UpdateProductSQLResultRequest;
import aforo.productrateplanservice.product.service.ProductSQLResultService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product - SQL Result Type")
public class ProductSQLResultResource {

    private final ProductSQLResultService productSQLResultService;

    @PostMapping("/{productId}/sql-result")
    public ResponseEntity<ProductSQLResultDTO> create(@PathVariable Long productId,
                                                      @RequestBody CreateProductSQLResultRequest request) {
        return ResponseEntity.ok(productSQLResultService.create(productId, request));
    }

    @GetMapping("/{productId}/sql-result")
    public ResponseEntity<ProductSQLResultDTO> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(productSQLResultService.getByProductId(productId));
    }

    @GetMapping("/sql-result")
    public ResponseEntity<List<ProductSQLResultDTO>> getAll() {
        return ResponseEntity.ok(productSQLResultService.getAll());
    }
@PutMapping("/{productId}/sql-result")
public ProductSQLResultDTO fullUpdate(@PathVariable Long productId, @RequestBody @Valid UpdateProductSQLResultRequest request) {
    return productSQLResultService.update(productId, request);
}

@PatchMapping("/{productId}/sql-result")
public ProductSQLResultDTO partialUpdate(@PathVariable Long productId, @RequestBody UpdateProductSQLResultRequest request) {
    return productSQLResultService.partialUpdate(productId, request);
}


    @DeleteMapping("/{productId}/sql-result")
    public ResponseEntity<Void> delete(@PathVariable Long productId) {
        productSQLResultService.delete(productId);
        return ResponseEntity.noContent().build();
    }
}
