package aforo.productrateplanservice.product.resource;

import aforo.productrateplanservice.product.dto.ProductFlatFileDTO;
import aforo.productrateplanservice.product.request.CreateProductFlatFileRequest;
import aforo.productrateplanservice.product.request.UpdateProductFlatFileRequest;
import aforo.productrateplanservice.product.service.ProductFlatFileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://13.115.248.133", "http://13.115.248.133:3001", "https://13.115.248.133", "https://13.115.248.133:3001"}, allowCredentials = "true")
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

    @PutMapping("/{productId}/flatfile")
public ProductFlatFileDTO fullUpdate(@PathVariable Long productId, @RequestBody @Valid UpdateProductFlatFileRequest request) {
    return productFlatFileService.update(productId, request);
}

@PatchMapping("/{productId}/flatfile")
public ProductFlatFileDTO partialUpdate(@PathVariable Long productId, @RequestBody UpdateProductFlatFileRequest request) {
    return productFlatFileService.partialUpdate(productId, request);
}


    // ✅ Delete
    @DeleteMapping("/{productId}/flatfile")
    public ResponseEntity<Void> delete(@PathVariable Long productId) {
        productFlatFileService.delete(productId);
        return ResponseEntity.noContent().build();
    }
}
