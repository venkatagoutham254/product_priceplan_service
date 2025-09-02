package aforo.productrateplanservice.product.service;

import aforo.productrateplanservice.exception.NotFoundException;
import aforo.productrateplanservice.product.dto.ProductLLMTokenDTO;
import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.entity.ProductLLMToken;
import aforo.productrateplanservice.product.mapper.ProductLLMTokenMapper;
import aforo.productrateplanservice.product.repository.*;
import aforo.productrateplanservice.product.request.CreateProductLLMTokenRequest;
import aforo.productrateplanservice.product.request.UpdateProductLLMTokenRequest;
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
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product " + productId + " not found"));

        // validation: ensure no other config type exists
        if (llmTokenRepository.existsById(productId)) {
            throw new IllegalStateException("Product " + productId + " already has LLM Token configuration.");
        }
        if (productAPIRepository.existsById(productId) ||
            productFlatFileRepository.existsById(productId) ||
            productSQLResultRepository.existsById(productId) ||
            productStorageRepository.existsById(productId)) {
            throw new IllegalStateException(
                "Product " + productId + " already has a different configuration type. " +
                "A product can have only one configuration type."
            );
        }

        ProductLLMToken entity = ProductLLMToken.builder()
                .product(product)
                .modelName(request.getModelName())
                .endpointUrl(request.getEndpointUrl())
                .authType(request.getAuthType())
                .build();

        return mapper.toDTO(llmTokenRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductLLMTokenDTO getByProductId(Long productId) {
        ProductLLMToken entity = llmTokenRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("LLM Token configuration not found for product " + productId));
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductLLMTokenDTO> getAll() {
        return llmTokenRepository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public ProductLLMTokenDTO updateFully(Long productId, UpdateProductLLMTokenRequest request) {
        ProductLLMToken existing = llmTokenRepository.findById(productId)
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
        ProductLLMToken existing = llmTokenRepository.findById(productId)
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
        if (!llmTokenRepository.existsById(productId)) {
            throw new NotFoundException("LLM Token configuration not found for product " + productId);
        }
        llmTokenRepository.deleteById(productId);
    }
}
