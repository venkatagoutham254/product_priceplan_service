package aforo.productrateplanservice.product.service;

import aforo.productrateplanservice.product.dto.ProductSQLResultDTO;
import aforo.productrateplanservice.product.request.CreateProductSQLResultRequest;
import aforo.productrateplanservice.product.request.UpdateProductSQLResultRequest;

import java.util.List;

public interface ProductSQLResultService {
    ProductSQLResultDTO create(Long productId, CreateProductSQLResultRequest request);
    ProductSQLResultDTO getByProductId(Long productId);
    List<ProductSQLResultDTO> getAll();
    ProductSQLResultDTO update(Long productId, UpdateProductSQLResultRequest request);
    void delete(Long productId);
}
