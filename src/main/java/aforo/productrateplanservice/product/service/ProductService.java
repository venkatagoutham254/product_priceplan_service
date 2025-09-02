package aforo.productrateplanservice.product.service;

import aforo.productrateplanservice.product.dto.ProductDTO;
import aforo.productrateplanservice.product.request.CreateProductRequest;
import aforo.productrateplanservice.product.request.UpdateProductRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    ProductDTO createProduct(CreateProductRequest request);
    ProductDTO createProduct(CreateProductRequest request, MultipartFile icon);
    ProductDTO getProductById(Long productId);
    List<ProductDTO> getAllProducts();
    void deleteProduct(Long productId);
    ProductDTO updateProductFully(Long id, UpdateProductRequest request);
    ProductDTO updateProductPartially(Long id, UpdateProductRequest request);
    ProductDTO finalizeProduct(Long id);
    ProductDTO updateIcon(Long id, MultipartFile icon);
    void deleteIcon(Long id);
}