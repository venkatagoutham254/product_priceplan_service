package aforo.productrateplanservice.product.service;

import aforo.productrateplanservice.exception.NotFoundException;
import aforo.productrateplanservice.product.dto.ProductStorageDTO;
import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.entity.ProductStorage;
import aforo.productrateplanservice.product.mapper.ProductStorageMapper;
import aforo.productrateplanservice.product.repository.*;
import aforo.productrateplanservice.product.request.CreateProductStorageRequest;
import aforo.productrateplanservice.product.request.UpdateProductStorageRequest;
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
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product " + productId + " not found"));

        // Ensure no other config type exists
        if (storageRepository.existsById(productId)) {
            throw new IllegalStateException("Product " + productId + " already has Storage configuration.");
        }
        if (productAPIRepository.existsById(productId)
         || productFlatFileRepository.existsById(productId)
         || productSQLResultRepository.existsById(productId)
         || productLLMTokenRepository.existsById(productId)) {
            throw new IllegalStateException(
                    "Product " + productId + " already has a different configuration type. " +
                    "A product can have only one configuration type."
            );
        }

        ProductStorage entity = ProductStorage.builder()
                .product(product)
                .storageLocation(request.getStorageLocation())
                .authType(request.getAuthType())
                .build();

        return mapper.toDTO(storageRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductStorageDTO getByProductId(Long productId) {
        ProductStorage entity = storageRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Storage configuration not found for product " + productId));
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductStorageDTO> getAll() {
        return storageRepository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public ProductStorageDTO updateFully(Long productId, UpdateProductStorageRequest request) {
        ProductStorage existing = storageRepository.findById(productId)
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
        ProductStorage existing = storageRepository.findById(productId)
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
        if (!storageRepository.existsById(productId)) {
            throw new NotFoundException("Storage configuration not found for product " + productId);
        }
        storageRepository.deleteById(productId);
    }
}
