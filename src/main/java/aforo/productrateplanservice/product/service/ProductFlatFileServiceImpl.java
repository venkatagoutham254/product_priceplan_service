package aforo.productrateplanservice.product.service;

import aforo.productrateplanservice.exception.NotFoundException;
import aforo.productrateplanservice.product.dto.ProductFlatFileDTO;
import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.entity.ProductFlatFile;
import aforo.productrateplanservice.product.mapper.ProductFlatFileMapper;
import aforo.productrateplanservice.product.repository.*;
import aforo.productrateplanservice.product.request.CreateProductFlatFileRequest;
import aforo.productrateplanservice.product.request.UpdateProductFlatFileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductFlatFileServiceImpl implements ProductFlatFileService {

    private final ProductFlatFileRepository flatFileRepository;
    private final ProductRepository productRepository;
    private final ProductAPIRepository productAPIRepository;
    private final ProductLLMTokenRepository productLLMTokenRepository;
    private final ProductSQLResultRepository productSQLResultRepository;
    private final ProductStorageRepository productStorageRepository;
    private final ProductFlatFileMapper mapper;

    @Override
    @Transactional
    public ProductFlatFileDTO create(Long productId, CreateProductFlatFileRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product " + productId + " not found"));

        // Ensure only one config type per product
        if (flatFileRepository.existsById(productId)) {
            throw new IllegalStateException("Product " + productId + " already has FlatFile configuration.");
        }
        if (productAPIRepository.existsById(productId)
         || productLLMTokenRepository.existsById(productId)
         || productSQLResultRepository.existsById(productId)
         || productStorageRepository.existsById(productId)) {
            throw new IllegalStateException(
                    "Product " + productId + " already has a different configuration type. " +
                    "A product can only have one configuration type."
            );
        }

        ProductFlatFile entity = ProductFlatFile.builder()
                .product(product)
                .fileLocation(request.getFileLocation())
                .format(request.getFormat())
                .build();

        return mapper.toDTO(flatFileRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductFlatFileDTO getByProductId(Long productId) {
        ProductFlatFile entity = flatFileRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("FlatFile configuration not found for product " + productId));
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductFlatFileDTO> getAll() {
        return flatFileRepository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public ProductFlatFileDTO update(Long productId, UpdateProductFlatFileRequest request) {
        ProductFlatFile existing = flatFileRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("FlatFile configuration not found for product " + productId));

        // Full update requires both fields
        if (request.getFileLocation() == null || request.getFormat() == null) {
            throw new IllegalArgumentException("fileLocation and format are required for full update.");
        }

        existing.setFileLocation(request.getFileLocation());
        existing.setFormat(request.getFormat());

        return mapper.toDTO(flatFileRepository.save(existing));
    }

    @Override
    @Transactional
    public ProductFlatFileDTO partialUpdate(Long productId, UpdateProductFlatFileRequest request) {
        ProductFlatFile existing = flatFileRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("FlatFile configuration not found for product " + productId));

        if (request.getFileLocation() != null) existing.setFileLocation(request.getFileLocation());
        if (request.getFormat() != null) existing.setFormat(request.getFormat());

        return mapper.toDTO(flatFileRepository.save(existing));
    }

    @Override
    @Transactional
    public void delete(Long productId) {
        if (!flatFileRepository.existsById(productId)) {
            throw new NotFoundException("FlatFile configuration not found for product " + productId);
        }
        flatFileRepository.deleteById(productId);
    }
}
