package aforo.productrateplanservice.product.resource;

import aforo.productrateplanservice.product.dto.ProductStorageDTO;
import aforo.productrateplanservice.product.request.CreateProductStorageRequest;
import aforo.productrateplanservice.product.request.UpdateProductStorageRequest;
import aforo.productrateplanservice.product.service.ProductStorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product - Storage Type")
public class ProductStorageResource {

    private final ProductStorageService productStorageService;

    @PostMapping("/{productId}/storage")
    public ResponseEntity<ProductStorageDTO> create(@PathVariable Long productId,
                                                    @RequestBody CreateProductStorageRequest request) {
        return ResponseEntity.status(201).body(productStorageService.create(productId, request));
    }

    @GetMapping("/{productId}/storage")
    public ResponseEntity<ProductStorageDTO> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(productStorageService.getByProductId(productId));
    }

    @GetMapping("/storage")
    public ResponseEntity<List<ProductStorageDTO>> getAll() {
        return ResponseEntity.ok(productStorageService.getAll());
    }

    @PutMapping("/{productId}/storage")
    public ResponseEntity<ProductStorageDTO> updateFully(
            @PathVariable Long productId,
            @RequestBody UpdateProductStorageRequest request) {
        ProductStorageDTO updated = productStorageService.updateFully(productId, request);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{productId}/storage")
    public ResponseEntity<ProductStorageDTO> updatePartially(
            @PathVariable Long productId,
            @RequestBody UpdateProductStorageRequest request) {
        ProductStorageDTO updated = productStorageService.updatePartially(productId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{productId}/storage")
    public ResponseEntity<Void> delete(@PathVariable Long productId) {
        productStorageService.delete(productId);
        return ResponseEntity.noContent().build();
    }
}
