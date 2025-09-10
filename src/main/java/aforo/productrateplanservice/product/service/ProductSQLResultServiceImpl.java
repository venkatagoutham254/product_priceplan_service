package aforo.productrateplanservice.product.service;

import aforo.productrateplanservice.exception.NotFoundException;
import aforo.productrateplanservice.product.dto.ProductSQLResultDTO;
import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.entity.ProductSQLResult;
import aforo.productrateplanservice.product.mapper.ProductSQLResultMapper;
import aforo.productrateplanservice.product.repository.*;
import aforo.productrateplanservice.product.request.CreateProductSQLResultRequest;
import aforo.productrateplanservice.product.request.UpdateProductSQLResultRequest;
import aforo.productrateplanservice.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSQLResultServiceImpl implements ProductSQLResultService {

    private final ProductSQLResultRepository sqlResultRepository;
    private final ProductRepository productRepository;
    private final ProductAPIRepository productAPIRepository;
    private final ProductFlatFileRepository productFlatFileRepository;
    private final ProductLLMTokenRepository productLLMTokenRepository;
    private final ProductStorageRepository productStorageRepository;
    private final ProductSQLResultMapper mapper;

    @Override
    @Transactional
    public ProductSQLResultDTO create(Long productId, CreateProductSQLResultRequest request) {
        Long orgId = TenantContext.require();
        Product product = productRepository.findByProductIdAndOrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("Product " + productId + " not found"));

        // Ensure only one config type per product
        if (sqlResultRepository.existsByProduct_ProductId(productId)) {
            throw new IllegalStateException("Product " + productId + " already has SQL Result configuration.");
        }
        if (productAPIRepository.existsByProduct_ProductId(productId)
         || productFlatFileRepository.existsByProduct_ProductId(productId)
         || productLLMTokenRepository.existsByProduct_ProductId(productId)
         || productStorageRepository.existsByProduct_ProductId(productId)) {
            throw new IllegalStateException(
                    "Product " + productId + " already has a different configuration type. " +
                    "A product can only have one configuration type."
            );
        }

        ProductSQLResult entity = ProductSQLResult.builder()
                .product(product)
                .dbType(request.getDbType())
                .connectionString(request.getConnectionString())
                .authType(request.getAuthType())
                .build();

        return mapper.toDTO(sqlResultRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductSQLResultDTO getByProductId(Long productId) {
        Long orgId = TenantContext.require();
        ProductSQLResult entity = sqlResultRepository
                .findByProduct_ProductIdAndProduct_OrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("SQL Result configuration not found for product " + productId));
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSQLResultDTO> getAll() {
        Long orgId = TenantContext.require();
        return sqlResultRepository.findAllByProduct_OrganizationId(orgId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public ProductSQLResultDTO update(Long productId, UpdateProductSQLResultRequest request) {
        Long orgId = TenantContext.require();
        ProductSQLResult existing = sqlResultRepository
                .findByProduct_ProductIdAndProduct_OrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("SQL Result configuration not found for product " + productId));

        // Require all fields for full update
        if (request.getDbType() == null || request.getConnectionString() == null || request.getAuthType() == null) {
            throw new IllegalArgumentException("dbType, connectionString, and authType are required for full update.");
        }

        existing.setDbType(request.getDbType());
        existing.setConnectionString(request.getConnectionString());
        existing.setAuthType(request.getAuthType());

        return mapper.toDTO(sqlResultRepository.save(existing));
    }

    @Override
    @Transactional
    public ProductSQLResultDTO partialUpdate(Long productId, UpdateProductSQLResultRequest request) {
        Long orgId = TenantContext.require();
        ProductSQLResult existing = sqlResultRepository
                .findByProduct_ProductIdAndProduct_OrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("SQL Result configuration not found for product " + productId));

        if (request.getDbType() != null) existing.setDbType(request.getDbType());
        if (request.getConnectionString() != null) existing.setConnectionString(request.getConnectionString());
        if (request.getAuthType() != null) existing.setAuthType(request.getAuthType());

        return mapper.toDTO(sqlResultRepository.save(existing));
    }

    @Override
    @Transactional
    public void delete(Long productId) {
        Long orgId = TenantContext.require();
        sqlResultRepository
            .findByProduct_ProductIdAndProduct_OrganizationId(productId, orgId)
            .orElseThrow(() -> new NotFoundException("SQL Result configuration not found for product " + productId));
        sqlResultRepository.deleteById(productId);
    }
}
