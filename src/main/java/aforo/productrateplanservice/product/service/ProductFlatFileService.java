package aforo.productrateplanservice.product.service;

import aforo.productrateplanservice.product.dto.ProductFlatFileDTO;
import aforo.productrateplanservice.product.request.CreateProductFlatFileRequest;
import aforo.productrateplanservice.product.request.UpdateProductFlatFileRequest;

import java.util.List;

public interface ProductFlatFileService {
    ProductFlatFileDTO create(Long productId, CreateProductFlatFileRequest request);
    ProductFlatFileDTO getByProductId(Long productId);
    List<ProductFlatFileDTO> getAll();
    ProductFlatFileDTO update(Long productId, UpdateProductFlatFileRequest request);
    ProductFlatFileDTO partialUpdate(Long productId, UpdateProductFlatFileRequest request);
        void delete(Long productId);
}
