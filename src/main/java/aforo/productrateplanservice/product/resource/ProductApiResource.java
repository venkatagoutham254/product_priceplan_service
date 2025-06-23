package aforo.productrateplanservice.product.resource;

import aforo.productrateplanservice.product.dto.ProductAPIDTO;
import aforo.productrateplanservice.product.request.CreateProductAPIRequest;
import aforo.productrateplanservice.product.request.UpdateProductAPIRequest;
import aforo.productrateplanservice.product.service.ProductAPIService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product - API Type")
public class ProductApiResource {

    private final ProductAPIService productAPIService;

    @PostMapping("/{productId}/api")
    public ResponseEntity<ProductAPIDTO> create(@PathVariable Long productId,
                                                @RequestBody CreateProductAPIRequest request) {
        return ResponseEntity.ok(productAPIService.create(productId, request));
    }

    @GetMapping("/{productId}/api")
    public ResponseEntity<ProductAPIDTO> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(productAPIService.getByProductId(productId));
    }

    @GetMapping("/api")
    public ResponseEntity<List<ProductAPIDTO>> getAll() {
        return ResponseEntity.ok(productAPIService.getAll());
    }

    @PutMapping("/{productId}/api")
    public ResponseEntity<ProductAPIDTO> update(@PathVariable Long productId,
                                                @RequestBody UpdateProductAPIRequest request) {
        return ResponseEntity.ok(productAPIService.update(productId, request));
    }

    @DeleteMapping("/{productId}/api")
    public ResponseEntity<Void> delete(@PathVariable Long productId) {
        productAPIService.delete(productId);
        return ResponseEntity.noContent().build();
    }
}
