package aforo.productrateplanservice.product.service;

import aforo.productrateplanservice.product.dto.ProductAPIDTO;
import aforo.productrateplanservice.product.request.CreateProductAPIRequest;
import aforo.productrateplanservice.product.request.UpdateProductAPIRequest;

import java.util.List;

public interface ProductAPIService {
    ProductAPIDTO create(Long productId, CreateProductAPIRequest request);
    ProductAPIDTO getByProductId(Long productId);
    List<ProductAPIDTO> getAll();
    ProductAPIDTO update(Long productId, UpdateProductAPIRequest request);
    void delete(Long productId);
}
