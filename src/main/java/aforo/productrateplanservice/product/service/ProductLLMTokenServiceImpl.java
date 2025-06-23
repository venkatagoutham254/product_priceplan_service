package aforo.productrateplanservice.product.service;

import aforo.productrateplanservice.product.dto.ProductLLMTokenDTO;
import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.entity.ProductLLMToken;
import aforo.productrateplanservice.product.enums.ProductType;
import aforo.productrateplanservice.product.mapper.ProductLLMTokenMapper;
import aforo.productrateplanservice.product.repository.ProductLLMTokenRepository;
import aforo.productrateplanservice.product.repository.ProductRepository;
import aforo.productrateplanservice.product.request.CreateProductLLMTokenRequest;
import aforo.productrateplanservice.product.request.UpdateProductLLMTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductLLMTokenServiceImpl implements ProductLLMTokenService {

    private final ProductLLMTokenRepository repository;
    private final ProductRepository productRepository;
    private final ProductLLMTokenMapper mapper;

    private void validateProductType(Product product, ProductType expected) {
        if (product.getProductType() != expected) {
            throw new RuntimeException("Invalid product type. Expected: " + expected);
        }
    }

    @Override
    public ProductLLMTokenDTO create(Long productId, CreateProductLLMTokenRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        validateProductType(product, ProductType.LLMToken);

        ProductLLMToken entity = ProductLLMToken.builder()
                .product(product)
                .tokenProvider(request.getTokenProvider())
                .modelName(request.getModelName())
                .tokenUnitCost(request.getTokenUnitCost())
                .calculationMethod(request.getCalculationMethod())
                .quota(request.getQuota())
                .promptTemplate(request.getPromptTemplate())
                .inferencePriority(request.getInferencePriority())
                .computeTier(request.getComputeTier())
                .build();

        return mapper.toDTO(repository.save(entity));
    }

    @Override
    public ProductLLMTokenDTO getByProductId(Long productId) {
        ProductLLMToken entity = repository.findById(productId)
                .orElseThrow(() -> new RuntimeException("LLM Token product not found"));
        return mapper.toDTO(entity);
    }

    @Override
    public List<ProductLLMTokenDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public ProductLLMTokenDTO update(Long productId, UpdateProductLLMTokenRequest request) {
        ProductLLMToken existing = repository.findById(productId)
                .orElseThrow(() -> new RuntimeException("LLM Token not found"));

        if (request.getTokenProvider() != null) existing.setTokenProvider(request.getTokenProvider());
        if (request.getModelName() != null) existing.setModelName(request.getModelName());
        if (request.getTokenUnitCost() != null) existing.setTokenUnitCost(request.getTokenUnitCost());
        if (request.getCalculationMethod() != null) existing.setCalculationMethod(request.getCalculationMethod());
        if (request.getQuota() != null) existing.setQuota(request.getQuota());
        if (request.getPromptTemplate() != null) existing.setPromptTemplate(request.getPromptTemplate());
        if (request.getInferencePriority() != null) existing.setInferencePriority(request.getInferencePriority());
        if (request.getComputeTier() != null) existing.setComputeTier(request.getComputeTier());

        return mapper.toDTO(repository.save(existing));
    }

    @Override
    public void delete(Long productId) {
        repository.deleteById(productId);
    }
}
