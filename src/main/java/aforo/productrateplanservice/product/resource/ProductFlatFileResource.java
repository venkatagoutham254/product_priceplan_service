package aforo.productrateplanservice.product.resource;

import aforo.productrateplanservice.product.dto.ProductFlatFileDTO;
import aforo.productrateplanservice.product.request.CreateProductFlatFileRequest;
import aforo.productrateplanservice.product.request.UpdateProductFlatFileRequest;
import aforo.productrateplanservice.product.service.ProductFlatFileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product - Flat File Type")
public class ProductFlatFileResource {

    private final ProductFlatFileService productFlatFileService;

    // ✅ Create
    @PostMapping("/{productId}/flatfile")
    public ResponseEntity<ProductFlatFileDTO> create(@PathVariable Long productId,
                                                     @RequestBody CreateProductFlatFileRequest request) {
        return ResponseEntity.ok(productFlatFileService.create(productId, request));
    }

    // ✅ Get by productId
    @GetMapping("/{productId}/flatfile")
    public ResponseEntity<ProductFlatFileDTO> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(productFlatFileService.getByProductId(productId));
    }

    // ✅ Get All
    @GetMapping("/flatfile")
    public ResponseEntity<List<ProductFlatFileDTO>> getAll() {
        return ResponseEntity.ok(productFlatFileService.getAll());
    }

    // ✅ Update
    @PutMapping("/{productId}/flatfile")
    public ResponseEntity<ProductFlatFileDTO> update(@PathVariable Long productId,
                                                     @RequestBody UpdateProductFlatFileRequest request) {
        return ResponseEntity.ok(productFlatFileService.update(productId, request));
    }

    // ✅ Delete
    @DeleteMapping("/{productId}/flatfile")
    public ResponseEntity<Void> delete(@PathVariable Long productId) {
        productFlatFileService.delete(productId);
        return ResponseEntity.noContent().build();
    }
}
