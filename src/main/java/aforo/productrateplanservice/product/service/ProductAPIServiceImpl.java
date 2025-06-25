package aforo.productrateplanservice.product.service;

import aforo.productrateplanservice.product.dto.ProductAPIDTO;
import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.entity.ProductAPI;
import aforo.productrateplanservice.product.enums.ProductType;
import aforo.productrateplanservice.product.mapper.ProductAPIMapper;
import aforo.productrateplanservice.product.repository.ProductAPIRepository;
import aforo.productrateplanservice.product.repository.ProductRepository;
import aforo.productrateplanservice.product.request.CreateProductAPIRequest;
import aforo.productrateplanservice.product.request.UpdateProductAPIRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductAPIServiceImpl implements ProductAPIService {

    private final ProductAPIRepository productAPIRepository;
    private final ProductRepository productRepository;
    private final ProductAPIMapper productAPIMapper;

    private void validateProductType(Product product, ProductType expected) {
        if (product.getProductType() != expected) {
            throw new RuntimeException("Invalid product type. Expected: " + expected + ", but got: " + product.getProductType());
        }
    }

    @Override
    public ProductAPIDTO create(Long productId, CreateProductAPIRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        validateProductType(product, ProductType.API);

        ProductAPI productAPI = ProductAPI.builder()
                .product(product)
                .endpointUrl(request.getEndpointUrl())
                .authType(request.getAuthType())
                .payloadSizeMetric(request.getPayloadSizeMetric())
                .rateLimitPolicy(request.getRateLimitPolicy())
                .meteringGranularity(request.getMeteringGranularity())
                .grouping(request.getGrouping())
                .cachingFlag(request.isCachingFlag())
                .latencyClass(request.getLatencyClass())
                .build();

        return productAPIMapper.toDTO(productAPIRepository.save(productAPI));
    }

    @Override
    public ProductAPIDTO getByProductId(Long productId) {
        ProductAPI api = productAPIRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product API not found for ID: " + productId));
        return productAPIMapper.toDTO(api);
    }

    @Override
    public List<ProductAPIDTO> getAll() {
        return productAPIRepository.findAll()
                .stream()
                .map(productAPIMapper::toDTO)
                .toList();
    }

    @Override
    public ProductAPIDTO updateFully(Long productId, UpdateProductAPIRequest request) {
        ProductAPI existing = productAPIRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("API product not found"));
    
        // Required fields check for full update
        if (request.getEndpointUrl() == null || request.getAuthType() == null) {
            throw new RuntimeException("Endpoint and Method are required for full update");
        }
    
        existing.setEndpointUrl(request.getEndpointUrl());
        existing.setAuthType(request.getAuthType());
        existing.setPayloadSizeMetric(request.getPayloadSizeMetric());
        existing.setRateLimitPolicy(request.getRateLimitPolicy());
        existing.setMeteringGranularity(request.getMeteringGranularity());
        existing.setGrouping(request.getGrouping());
        existing.setCachingFlag(request.getCachingFlag());
        existing.setLatencyClass(request.getLatencyClass());
    
        return productAPIMapper.toDTO(productAPIRepository.save(existing));
    }
    
    @Override
    public ProductAPIDTO updatePartially(Long productId, UpdateProductAPIRequest request) {
        ProductAPI existing = productAPIRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("API product not found"));
    
        if (request.getEndpointUrl() != null) existing.setEndpointUrl(request.getEndpointUrl());
        if (request.getAuthType() != null) existing.setAuthType(request.getAuthType());
        if (request.getPayloadSizeMetric() != null) existing.setPayloadSizeMetric(request.getPayloadSizeMetric());
        if (request.getRateLimitPolicy() != null) existing.setRateLimitPolicy(request.getRateLimitPolicy());
        if (request.getMeteringGranularity() != null) existing.setMeteringGranularity(request.getMeteringGranularity());
        if (request.getGrouping() != null) existing.setGrouping(request.getGrouping());
        if (request.getCachingFlag() != null) existing.setCachingFlag(request.getCachingFlag());
        if (request.getLatencyClass() != null) existing.setLatencyClass(request.getLatencyClass());
    
        return productAPIMapper.toDTO(productAPIRepository.save(existing));
    }
    
    @Override
    public void delete(Long productId) {
        productAPIRepository.deleteById(productId);
    }
}
