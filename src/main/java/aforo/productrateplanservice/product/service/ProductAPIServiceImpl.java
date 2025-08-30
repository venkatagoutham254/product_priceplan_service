package aforo.productrateplanservice.product.service;

import aforo.productrateplanservice.exception.NotFoundException;
import aforo.productrateplanservice.product.dto.ProductAPIDTO;
import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.entity.ProductAPI;
import aforo.productrateplanservice.product.mapper.ProductAPIMapper;
import aforo.productrateplanservice.product.repository.ProductAPIRepository;
import aforo.productrateplanservice.product.repository.ProductRepository;
import aforo.productrateplanservice.product.request.CreateProductAPIRequest;
import aforo.productrateplanservice.product.request.UpdateProductAPIRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductAPIServiceImpl implements ProductAPIService {

    private final ProductAPIRepository productAPIRepository;
    private final ProductRepository productRepository;
    private final ProductAPIMapper productAPIMapper;

    @Override
    @Transactional
    public ProductAPIDTO create(Long productId, CreateProductAPIRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product " + productId + " not found"));

        // ensure one API config per product
        if (productAPIRepository.existsById(productId)) {
            throw new IllegalStateException("Product " + productId + " already has API configuration.");
        }

        ProductAPI productAPI = ProductAPI.builder()
                .product(product)
                .endpointUrl(request.getEndpointUrl())
                .authType(request.getAuthType())
                .build();

        return productAPIMapper.toDTO(productAPIRepository.save(productAPI));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductAPIDTO getByProductId(Long productId) {
        ProductAPI api = productAPIRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("API configuration not found for product " + productId));
        return productAPIMapper.toDTO(api);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductAPIDTO> getAll() {
        return productAPIRepository.findAll()
                .stream()
                .map(productAPIMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public ProductAPIDTO updateFully(Long productId, UpdateProductAPIRequest request) {
        ProductAPI existing = productAPIRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("API configuration not found for product " + productId));

        // PUT = both fields required
        if (request.getEndpointUrl() == null || request.getAuthType() == null) {
            throw new IllegalArgumentException("endpointUrl and authType are required for full update.");
        }

        existing.setEndpointUrl(request.getEndpointUrl());
        existing.setAuthType(request.getAuthType());

        return productAPIMapper.toDTO(productAPIRepository.save(existing));
    }

    @Override
    @Transactional
    public ProductAPIDTO updatePartially(Long productId, UpdateProductAPIRequest request) {
        ProductAPI existing = productAPIRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("API configuration not found for product " + productId));

        if (request.getEndpointUrl() != null) {
            existing.setEndpointUrl(request.getEndpointUrl());
        }
        if (request.getAuthType() != null) {
            existing.setAuthType(request.getAuthType());
        }

        return productAPIMapper.toDTO(productAPIRepository.save(existing));
    }

    @Override
    @Transactional
    public void delete(Long productId) {
        if (!productAPIRepository.existsById(productId)) {
            throw new NotFoundException("API configuration not found for product " + productId);
        }
        productAPIRepository.deleteById(productId);
    }
}
