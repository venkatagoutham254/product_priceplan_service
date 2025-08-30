package aforo.productrateplanservice.product.service;

import aforo.productrateplanservice.product.dto.ProductStorageDTO;
import aforo.productrateplanservice.product.request.CreateProductStorageRequest;
import aforo.productrateplanservice.product.request.UpdateProductStorageRequest;

import java.util.List;

public interface ProductStorageService {
    ProductStorageDTO create(Long productId, CreateProductStorageRequest request);
    ProductStorageDTO getByProductId(Long productId);
    List<ProductStorageDTO> getAll();
    ProductStorageDTO updateFully(Long productId, UpdateProductStorageRequest request);
    ProductStorageDTO updatePartially(Long productId, UpdateProductStorageRequest request);
    void delete(Long productId);
}
