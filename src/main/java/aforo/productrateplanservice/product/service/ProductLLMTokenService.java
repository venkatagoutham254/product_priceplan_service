package aforo.productrateplanservice.product.service;

import aforo.productrateplanservice.product.dto.ProductLLMTokenDTO;
import aforo.productrateplanservice.product.request.CreateProductLLMTokenRequest;
import aforo.productrateplanservice.product.request.UpdateProductLLMTokenRequest;

import java.util.List;

public interface ProductLLMTokenService {
    ProductLLMTokenDTO create(Long productId, CreateProductLLMTokenRequest request);
    ProductLLMTokenDTO getByProductId(Long productId);
    List<ProductLLMTokenDTO> getAll();
    ProductLLMTokenDTO update(Long productId, UpdateProductLLMTokenRequest request);
    void delete(Long productId);
}
