package aforo.productrateplanservice.product.resource;

import aforo.productrateplanservice.product.dto.ProductDTO;
import aforo.productrateplanservice.product.request.CreateProductRequest;
import aforo.productrateplanservice.product.request.UpdateProductRequest;
import aforo.productrateplanservice.product.request.ProductCreateMultipart;
import aforo.productrateplanservice.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://13.115.248.133", "http://13.115.248.133:3001", "https://13.115.248.133", "https://13.115.248.133:3001"}, allowCredentials = "true")
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductResource {

    private final ProductService productService;
    private final ObjectMapper objectMapper;

    // Multipart variant to create product with optional icon file, like CustomerService style
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequestBody(
            description = "Multipart payload containing JSON 'request' and optional 'icon' file",
            required = true,
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(implementation = ProductCreateMultipart.class)
            )
    )
    public ResponseEntity<ProductDTO> createProductMultipart(
            @RequestPart("request") String requestJson,
            @RequestPart(value = "icon", required = false) MultipartFile icon) {
        try {
            CreateProductRequest request = objectMapper.readValue(requestJson, CreateProductRequest.class);
            return ResponseEntity.ok(productService.createProduct(request, icon));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON in 'request' part", e);
        }
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

    // Separate PATCH endpoint for just the icon
    @PatchMapping(path = "/{id}/icon", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDTO> updateIcon(
            @PathVariable Long id,
            @RequestPart("icon") MultipartFile icon) {
        ProductDTO updated = productService.updateIcon(id, icon);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // Delete only the icon
    @DeleteMapping("/{id}/icon")
    public ResponseEntity<Void> deleteIcon(@PathVariable Long id) {
        productService.deleteIcon(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/finalize")
    public ResponseEntity<ProductDTO> finalizeProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.finalizeProduct(id));
    }
}