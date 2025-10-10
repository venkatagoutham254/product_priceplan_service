package aforo.productrateplanservice.product.service;

import aforo.productrateplanservice.exception.NotFoundException;
import aforo.productrateplanservice.product.dto.ProductLLMTokenDTO;
import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.entity.ProductLLMToken;
import aforo.productrateplanservice.product.enums.ProductType;
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
    @Transactional
    public ProductLLMTokenDTO create(Long productId, CreateProductLLMTokenRequest request) {
        Long orgId = TenantContext.require();
        Product product = productRepository.findByProductIdAndOrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("Product " + productId + " not found"));

        if (llmTokenRepository.existsByProduct_ProductId(productId)) {
            throw new IllegalStateException("Product " + productId + " already has LLM Token configuration.");
        }
        if (productAPIRepository.existsByProduct_ProductId(productId)
                || productFlatFileRepository.existsByProduct_ProductId(productId)
                || productSQLResultRepository.existsByProduct_ProductId(productId)
                || productStorageRepository.existsByProduct_ProductId(productId)) {
            throw new IllegalStateException("Product " + productId + " already has a different configuration type.");
        }
        if (product.getProductType() != null && product.getProductType() != ProductType.LLMToken) {
            throw new IllegalStateException("Product type already set to " + product.getProductType());
        }

        ProductLLMToken entity = ProductLLMToken.builder()
                .product(product)
                .modelName(request.getModelName())
                .endpointUrl(request.getEndpointUrl())
                .authType(request.getAuthType())
                .build();

        ProductLLMTokenDTO dto = mapper.toDTO(llmTokenRepository.save(entity));

        product.setProductType(ProductType.LLMToken);
        productRepository.save(product);

        return dto;
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
                .stream().map(mapper::toDTO).toList();
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

        if (request.getModelName() != null) existing.setModelName(request.getModelName());
        if (request.getEndpointUrl() != null) existing.setEndpointUrl(request.getEndpointUrl());
        if (request.getAuthType() != null) existing.setAuthType(request.getAuthType());

        return mapper.toDTO(llmTokenRepository.save(existing));
    }

    @Override
    @Transactional
    public void delete(Long productId) {
        Long orgId = TenantContext.require();
        Product product = productRepository.findByProductIdAndOrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("Product " + productId + " not found"));

        llmTokenRepository
                .findByProduct_ProductIdAndProduct_OrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("LLM Token configuration not found for product " + productId));
        llmTokenRepository.deleteById(productId);

        if (product.getProductType() == ProductType.LLMToken) {
            product.setProductType(null);
            productRepository.save(product);
        }
    }
}
