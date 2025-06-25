package aforo.productrateplanservice.product.service;

import aforo.productrateplanservice.product.dto.ProductSQLResultDTO;
import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.entity.ProductSQLResult;
import aforo.productrateplanservice.product.enums.ProductType;
import aforo.productrateplanservice.product.mapper.ProductSQLResultMapper;
import aforo.productrateplanservice.product.repository.ProductRepository;
import aforo.productrateplanservice.product.repository.ProductSQLResultRepository;
import aforo.productrateplanservice.product.request.CreateProductSQLResultRequest;
import aforo.productrateplanservice.product.request.UpdateProductSQLResultRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSQLResultServiceImpl implements ProductSQLResultService {

    private final ProductSQLResultRepository repository;
    private final ProductRepository productRepository;
    private final ProductSQLResultMapper mapper;

    private void validateProductType(Product product, ProductType expected) {
        if (product.getProductType() != expected) {
            throw new RuntimeException("Invalid product type. Expected: " + expected);
        }
    }

    @Override
    public ProductSQLResultDTO create(Long productId, CreateProductSQLResultRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        validateProductType(product, ProductType.SQLResult);

        ProductSQLResult entity = ProductSQLResult.builder()
                .product(product)
                .queryTemplate(request.getQueryTemplate())
                .dbType(request.getDbType())
                .resultSize(request.getResultSize())
                .freshness(request.getFreshness())
                .executionFrequency(request.getExecutionFrequency())
                .expectedRowRange(request.getExpectedRowRange())
                .isCached(request.isCached())
                .joinComplexity(request.getJoinComplexity())
                .build();

        return mapper.toDTO(repository.save(entity));
    }

    @Override
    public ProductSQLResultDTO getByProductId(Long productId) {
        ProductSQLResult entity = repository.findById(productId)
                .orElseThrow(() -> new RuntimeException("SQL Result not found"));
        return mapper.toDTO(entity);
    }

    @Override
    public List<ProductSQLResultDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public ProductSQLResultDTO update(Long productId, UpdateProductSQLResultRequest request) {
        ProductSQLResult existing = repository.findById(productId)
            .orElseThrow(() -> new RuntimeException("SQL Result not found"));
    
        mapper.updateEntity(existing, request);
        return mapper.toDTO(repository.save(existing));
    }
    
    @Override
    public ProductSQLResultDTO partialUpdate(Long productId, UpdateProductSQLResultRequest request) {
        ProductSQLResult existing = repository.findById(productId)
            .orElseThrow(() -> new RuntimeException("SQL Result not found"));
    
        mapper.partialUpdate(existing, request);
        return mapper.toDTO(repository.save(existing));
    }
    

    @Override
    public void delete(Long productId) {
        repository.deleteById(productId);
    }
}
