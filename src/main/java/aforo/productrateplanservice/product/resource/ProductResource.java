package aforo.productrateplanservice.product.resource;

import aforo.productrateplanservice.product.dto.ProductDTO;
import aforo.productrateplanservice.product.request.CreateProductRequest;
import aforo.productrateplanservice.product.request.UpdateProductRequest;
import aforo.productrateplanservice.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://13.115.248.133", "http://13.115.248.133:3001", "https://13.115.248.133", "https://13.115.248.133:3001"}, allowCredentials = "true")
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductResource {

    private final ProductService productService;
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

   @PutMapping("/{id}")
public ResponseEntity<ProductDTO> updateProductFully(
        @PathVariable Long id,
        @RequestBody @Valid UpdateProductRequest request) {
    ProductDTO updated = productService.updateProductFully(id, request);
    return ResponseEntity.ok(updated);
}

@PatchMapping("/{id}")
public ResponseEntity<ProductDTO> updateProductPartially(
        @PathVariable Long id,
        @RequestBody UpdateProductRequest request) {
    ProductDTO updated = productService.updateProductPartially(id, request);
    return ResponseEntity.ok(updated);
}


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{id}/finalize")
    public ResponseEntity<ProductDTO> finalizeProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.finalizeProduct(id));
    }
}