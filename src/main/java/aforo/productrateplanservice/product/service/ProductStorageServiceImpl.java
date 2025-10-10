package aforo.productrateplanservice.product.service;

import aforo.productrateplanservice.exception.NotFoundException;
import aforo.productrateplanservice.product.dto.ProductStorageDTO;
import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.entity.ProductStorage;
import aforo.productrateplanservice.product.enums.ProductType;
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

        if (storageRepository.existsByProduct_ProductId(productId)) {
            throw new IllegalStateException("Product " + productId + " already has Storage configuration.");
        }
        if (productAPIRepository.existsByProduct_ProductId(productId)
                || productFlatFileRepository.existsByProduct_ProductId(productId)
                || productSQLResultRepository.existsByProduct_ProductId(productId)
                || productLLMTokenRepository.existsByProduct_ProductId(productId)) {
            throw new IllegalStateException("Product " + productId + " already has a different configuration type.");
        }
        if (product.getProductType() != null && product.getProductType() != ProductType.Storage) {
            throw new IllegalStateException("Product type already set to " + product.getProductType());
        }

        ProductStorage entity = ProductStorage.builder()
                .product(product)
                .storageLocation(request.getStorageLocation())
                .authType(request.getAuthType())
                .build();

        ProductStorageDTO dto = mapper.toDTO(storageRepository.save(entity));

        product.setProductType(ProductType.Storage);
        productRepository.save(product);

        return dto;
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
                .stream().map(mapper::toDTO).toList();
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

        if (request.getStorageLocation() != null) existing.setStorageLocation(request.getStorageLocation());
        if (request.getAuthType() != null) existing.setAuthType(request.getAuthType());

        return mapper.toDTO(storageRepository.save(existing));
    }

    @Override
    @Transactional
    public void delete(Long productId) {
        Long orgId = TenantContext.require();
        Product product = productRepository.findByProductIdAndOrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("Product " + productId + " not found"));

        storageRepository
                .findByProduct_ProductIdAndProduct_OrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("Storage configuration not found for product " + productId));
        storageRepository.deleteById(productId);

        if (product.getProductType() == ProductType.Storage) {
            product.setProductType(null);
            productRepository.save(product);
        }
    }
}
