package aforo.productrateplanservice.product.resource;

import aforo.productrateplanservice.product.dto.ProductLLMTokenDTO;
import aforo.productrateplanservice.product.request.CreateProductLLMTokenRequest;
import aforo.productrateplanservice.product.request.UpdateProductLLMTokenRequest;
import aforo.productrateplanservice.product.service.ProductLLMTokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product - LLM Token Type")
public class ProductLLMTokenResource {

    private final ProductLLMTokenService productLLMTokenService;

    @PostMapping("/{productId}/llm-token")
    public ResponseEntity<ProductLLMTokenDTO> create(@PathVariable Long productId,
                                                     @RequestBody CreateProductLLMTokenRequest request) {
        return ResponseEntity.ok(productLLMTokenService.create(productId, request));
    }

    @GetMapping("/{productId}/llm-token")
    public ResponseEntity<ProductLLMTokenDTO> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(productLLMTokenService.getByProductId(productId));
    }

    @GetMapping("/llm-token")
    public ResponseEntity<List<ProductLLMTokenDTO>> getAll() {
        return ResponseEntity.ok(productLLMTokenService.getAll());
    }

    @PutMapping("/{productId}/llm-token")
    public ResponseEntity<ProductLLMTokenDTO> update(@PathVariable Long productId,
                                                     @RequestBody UpdateProductLLMTokenRequest request) {
        return ResponseEntity.ok(productLLMTokenService.update(productId, request));
    }

    @DeleteMapping("/{productId}/llm-token")
    public ResponseEntity<Void> delete(@PathVariable Long productId) {
        productLLMTokenService.delete(productId);
        return ResponseEntity.noContent().build();
    }
}
