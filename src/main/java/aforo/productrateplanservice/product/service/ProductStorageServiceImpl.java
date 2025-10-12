package aforo.productrateplanservice.product.service;

import aforo.productrateplanservice.exception.NotFoundException;
import aforo.productrateplanservice.product.dto.ProductStorageDTO;
import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.entity.ProductStorage;
import aforo.productrateplanservice.product.mapper.ProductStorageMapper;
import aforo.productrateplanservice.product.repository.*;
import aforo.productrateplanservice.product.request.CreateProductStorageRequest;
import aforo.productrateplanservice.product.request.UpdateProductStorageRequest;
import aforo.productrateplanservice.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductStorageServiceImpl implements ProductStorageService {

    private final ProductStorageRepository storageRepository;
    private final ProductRepository productRepository;
    private final ProductAPIRepository productAPIRepository;
    private final ProductFlatFileRepository productFlatFileRepository;
    private final ProductSQLResultRepository productSQLResultRepository;
    private final ProductLLMTokenRepository productLLMTokenRepository;
    private final ProductStorageMapper mapper;

    @Override
    @Transactional
    public ProductStorageDTO create(Long productId, CreateProductStorageRequest request) {
        Long orgId = TenantContext.require();
        Product product = productRepository.findByProductIdAndOrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("Product " + productId + " not found"));

        // Check if Storage config already exists
        if (storageRepository.existsByProduct_ProductId(productId)) {
            throw new IllegalStateException("Product " + productId + " already has Storage configuration. Please delete it first to change.");
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
        if (productSQLResultRepository.existsByProduct_ProductId(productId)) {
            productSQLResultRepository.deleteById(productId);
            hasOtherConfig = true;
        }
        if (productLLMTokenRepository.existsByProduct_ProductId(productId)) {
            productLLMTokenRepository.deleteById(productId);
            hasOtherConfig = true;
        }
        
        // Log if we cleared any existing configuration
        if (hasOtherConfig) {
            // Configuration was automatically cleared to allow type switch
            product.setProductType(null); // Clear the old type first
        }

        ProductStorage entity = ProductStorage.builder()
                .product(product)
                .storageLocation(request.getStorageLocation())
                .authType(request.getAuthType())
                .build();

        // Update the product type in the Product entity
        product.setProductType(aforo.productrateplanservice.product.enums.ProductType.Storage);
        productRepository.save(product);

        return mapper.toDTO(storageRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductStorageDTO getByProductId(Long productId) {
        Long orgId = TenantContext.require();
        ProductStorage entity = storageRepository
                .findByProduct_ProductIdAndProduct_OrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("Storage configuration not found for product " + productId));
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductStorageDTO> getAll() {
        Long orgId = TenantContext.require();
        return storageRepository.findAllByProduct_OrganizationId(orgId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public ProductStorageDTO updateFully(Long productId, UpdateProductStorageRequest request) {
        Long orgId = TenantContext.require();
        ProductStorage existing = storageRepository
                .findByProduct_ProductIdAndProduct_OrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("Storage configuration not found for product " + productId));

        if (request.getStorageLocation() == null || request.getAuthType() == null) {
            throw new IllegalArgumentException("storageLocation and authType are required for full update.");
        }

        existing.setStorageLocation(request.getStorageLocation());
        existing.setAuthType(request.getAuthType());

        return mapper.toDTO(storageRepository.save(existing));
    }

    @Override
    @Transactional
    public ProductStorageDTO updatePartially(Long productId, UpdateProductStorageRequest request) {
        Long orgId = TenantContext.require();
        ProductStorage existing = storageRepository
                .findByProduct_ProductIdAndProduct_OrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("Storage configuration not found for product " + productId));

        if (request.getStorageLocation() != null) {
            existing.setStorageLocation(request.getStorageLocation());
        }
        if (request.getAuthType() != null) {
            existing.setAuthType(request.getAuthType());
        }

        return mapper.toDTO(storageRepository.save(existing));
    }

    @Override
    @Transactional
    public void delete(Long productId) {
        Long orgId = TenantContext.require();
        storageRepository
            .findByProduct_ProductIdAndProduct_OrganizationId(productId, orgId)
            .orElseThrow(() -> new NotFoundException("Storage configuration not found for product " + productId));
        storageRepository.deleteById(productId);
    }
}
