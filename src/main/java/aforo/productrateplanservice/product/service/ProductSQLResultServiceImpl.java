package aforo.productrateplanservice.product.service;

import aforo.productrateplanservice.exception.NotFoundException;
import aforo.productrateplanservice.product.dto.ProductSQLResultDTO;
import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.entity.ProductSQLResult;
import aforo.productrateplanservice.product.mapper.ProductSQLResultMapper;
import aforo.productrateplanservice.product.repository.*;
import aforo.productrateplanservice.product.request.CreateProductSQLResultRequest;
import aforo.productrateplanservice.product.request.UpdateProductSQLResultRequest;
import aforo.productrateplanservice.product.util.ProductTypeValidator;
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
    private final SkuGenerationService skuGenerationService;

    @Override
    @Transactional
    public ProductSQLResultDTO create(Long productId, CreateProductSQLResultRequest request) {
        Long orgId = TenantContext.require();
        Product product = productRepository.findByProductIdAndOrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("Product " + productId + " not found"));

        // Validate connection string format based on database type
        ProductTypeValidator.validateConnectionString(request.getConnectionString(), request.getDbType());
        
        // Check if SQL Result config already exists
        if (sqlResultRepository.existsByProduct_ProductId(productId)) {
            throw new IllegalStateException("Product " + productId + " already has SQL Result configuration. Please delete it first to change.");
        }
        
        // Auto-clear other configuration types if they exist
        boolean hasOtherConfig = false;
        if (productAPIRepository.existsByProduct_ProductId(productId)) {
            productAPIRepository.deleteById(productId);
            hasOtherConfig = true;
        }
        if (productFlatFileRepository.existsByProduct_ProductId(productId)) {
            productFlatFileRepository.deleteById(productId);
            hasOtherConfig = true;
        }
        if (productLLMTokenRepository.existsByProduct_ProductId(productId)) {
            productLLMTokenRepository.deleteById(productId);
            hasOtherConfig = true;
        }
        if (productStorageRepository.existsByProduct_ProductId(productId)) {
            productStorageRepository.deleteById(productId);
            hasOtherConfig = true;
        }
        
        // Store old type for SKU regeneration
        aforo.productrateplanservice.product.enums.ProductType oldType = product.getProductType();
        
        // Log if we cleared any existing configuration
        if (hasOtherConfig) {
            // Configuration was automatically cleared to allow type switch
            product.setProductType(null); // Clear the old type first
        }

        ProductSQLResult entity = ProductSQLResult.builder()
                .product(product)
                .dbType(request.getDbType())
                .connectionString(request.getConnectionString())
                .authType(request.getAuthType())
                .build();

        // Update the product type in the Product entity
        product.setProductType(aforo.productrateplanservice.product.enums.ProductType.SQLResult);
        
        // Auto-regenerate SKU if product type changed
        if (skuGenerationService.shouldRegenerateSku(product, product.getProductName(), oldType)) {
            String newSku = skuGenerationService.updateSkuCode(product);
            product.setInternalSkuCode(newSku);
        }
        
        productRepository.save(product);

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
        
        // Validate connection string format
        ProductTypeValidator.validateConnectionString(request.getConnectionString(), request.getDbType());

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

        if (request.getDbType() != null || request.getConnectionString() != null) {
            // Get the effective values (new or existing)
            aforo.productrateplanservice.product.enums.DBType effectiveDbType = 
                request.getDbType() != null ? request.getDbType() : existing.getDbType();
            String effectiveConnString = 
                request.getConnectionString() != null ? request.getConnectionString() : existing.getConnectionString();
            
            // Validate the combination
            ProductTypeValidator.validateConnectionString(effectiveConnString, effectiveDbType);
            
            if (request.getDbType() != null) existing.setDbType(request.getDbType());
            if (request.getConnectionString() != null) existing.setConnectionString(request.getConnectionString());
        }
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
