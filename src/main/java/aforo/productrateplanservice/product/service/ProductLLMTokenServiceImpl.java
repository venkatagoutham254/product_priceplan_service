package aforo.productrateplanservice.product.service;

import aforo.productrateplanservice.exception.NotFoundException;
import aforo.productrateplanservice.product.dto.ProductLLMTokenDTO;
import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.entity.ProductLLMToken;
import aforo.productrateplanservice.product.mapper.ProductLLMTokenMapper;
import aforo.productrateplanservice.product.repository.*;
import aforo.productrateplanservice.product.request.CreateProductLLMTokenRequest;
import aforo.productrateplanservice.product.request.UpdateProductLLMTokenRequest;
import aforo.productrateplanservice.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductLLMTokenServiceImpl implements ProductLLMTokenService {

    private final ProductLLMTokenRepository llmTokenRepository;
    private final ProductRepository productRepository;
    private final ProductAPIRepository productAPIRepository;
    private final ProductFlatFileRepository productFlatFileRepository;
    private final ProductSQLResultRepository productSQLResultRepository;
    private final ProductStorageRepository productStorageRepository;
    private final ProductLLMTokenMapper mapper;

    @Override
    public ProductLLMTokenDTO create(Long productId, CreateProductLLMTokenRequest request) {
        Long orgId = TenantContext.require();
        Product product = productRepository.findByProductIdAndOrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("Product " + productId + " not found"));

        // Check if LLMToken config already exists
        if (llmTokenRepository.existsByProduct_ProductId(productId)) {
            throw new IllegalStateException("Product " + productId + " already has LLMToken configuration. Please delete it first to change.");
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
        if (productStorageRepository.existsByProduct_ProductId(productId)) {
            productStorageRepository.deleteById(productId);
            hasOtherConfig = true;
        }
        
        // Log if we cleared any existing configuration
        if (hasOtherConfig) {
            // Configuration was automatically cleared to allow type switch
            product.setProductType(null); // Clear the old type first
        }

        ProductLLMToken entity = ProductLLMToken.builder()
                .product(product)
                .modelName(request.getModelName())
                .endpointUrl(request.getEndpointUrl())
                .authType(request.getAuthType())
                .build();

        // Update the product type in the Product entity
        product.setProductType(aforo.productrateplanservice.product.enums.ProductType.LLMToken);
        productRepository.save(product);

        return mapper.toDTO(llmTokenRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductLLMTokenDTO getByProductId(Long productId) {
        Long orgId = TenantContext.require();
        ProductLLMToken entity = llmTokenRepository
                .findByProduct_ProductIdAndProduct_OrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("LLM Token configuration not found for product " + productId));
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductLLMTokenDTO> getAll() {
        Long orgId = TenantContext.require();
        return llmTokenRepository.findAllByProduct_OrganizationId(orgId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public ProductLLMTokenDTO updateFully(Long productId, UpdateProductLLMTokenRequest request) {
        Long orgId = TenantContext.require();
        ProductLLMToken existing = llmTokenRepository
                .findByProduct_ProductIdAndProduct_OrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("LLM Token configuration not found for product " + productId));

        if (request.getModelName() == null || request.getEndpointUrl() == null || request.getAuthType() == null) {
            throw new IllegalArgumentException("modelName, endpointUrl and authType are required for full update.");
        }

        existing.setModelName(request.getModelName());
        existing.setEndpointUrl(request.getEndpointUrl());
        existing.setAuthType(request.getAuthType());

        return mapper.toDTO(llmTokenRepository.save(existing));
    }

    @Override
    @Transactional
    public ProductLLMTokenDTO updatePartially(Long productId, UpdateProductLLMTokenRequest request) {
        Long orgId = TenantContext.require();
        ProductLLMToken existing = llmTokenRepository
                .findByProduct_ProductIdAndProduct_OrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("LLM Token configuration not found for product " + productId));

        if (request.getModelName() != null) {
            existing.setModelName(request.getModelName());
        }
        if (request.getEndpointUrl() != null) {
            existing.setEndpointUrl(request.getEndpointUrl());
        }
        if (request.getAuthType() != null) {
            existing.setAuthType(request.getAuthType());
        }

        return mapper.toDTO(llmTokenRepository.save(existing));
    }

    @Override
    @Transactional
    public void delete(Long productId) {
        Long orgId = TenantContext.require();
        llmTokenRepository
            .findByProduct_ProductIdAndProduct_OrganizationId(productId, orgId)
            .orElseThrow(() -> new NotFoundException("LLM Token configuration not found for product " + productId));
        llmTokenRepository.deleteById(productId);
    }
}
