package aforo.productrateplanservice.product.service;

import aforo.productrateplanservice.product.dto.ProductFlatFileDTO;
import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.entity.ProductFlatFile;
import aforo.productrateplanservice.product.enums.ProductType;
import aforo.productrateplanservice.product.mapper.ProductFlatFileMapper;
import aforo.productrateplanservice.product.repository.ProductFlatFileRepository;
import aforo.productrateplanservice.product.repository.ProductRepository;
import aforo.productrateplanservice.product.request.CreateProductFlatFileRequest;
import aforo.productrateplanservice.product.request.UpdateProductFlatFileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductFlatFileServiceImpl implements ProductFlatFileService {

    private final ProductFlatFileRepository repository;
    private final ProductRepository productRepository;
    private final ProductFlatFileMapper mapper;

    private void validateProductType(Product product, ProductType expected) {
        if (product.getProductType() != expected) {
            throw new RuntimeException("Invalid product type. Expected: " + expected);
        }
    }

    @Override
    public ProductFlatFileDTO create(Long productId, CreateProductFlatFileRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        validateProductType(product, ProductType.FlatFile);

        ProductFlatFile entity = ProductFlatFile.builder()
                .product(product)
                .format(request.getFormat())
                .size(request.getSize())
                .deliveryFrequency(request.getDeliveryFrequency())
                .accessMethod(request.getAccessMethod())
                .retentionPolicy(request.getRetentionPolicy())
                .fileNamingConvention(request.getFileNamingConvention())
                .compressionFormat(request.getCompressionFormat())
                .build();

        return mapper.toDTO(repository.save(entity));
    }

    @Override
    public ProductFlatFileDTO getByProductId(Long productId) {
        ProductFlatFile entity = repository.findById(productId)
                .orElseThrow(() -> new RuntimeException("FlatFile not found"));
        return mapper.toDTO(entity);
    }

    @Override
    public List<ProductFlatFileDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public ProductFlatFileDTO update(Long productId, UpdateProductFlatFileRequest request) {
        ProductFlatFile existing = repository.findById(productId)
            .orElseThrow(() -> new RuntimeException("FlatFile not found"));
    
        mapper.updateEntity(existing, request);
        return mapper.toDTO(repository.save(existing));
    }
    
    @Override
    public ProductFlatFileDTO partialUpdate(Long productId, UpdateProductFlatFileRequest request) {
        ProductFlatFile existing = repository.findById(productId)
            .orElseThrow(() -> new RuntimeException("FlatFile not found"));
    
        mapper.partialUpdate(existing, request);
        return mapper.toDTO(repository.save(existing));
    }
    

    @Override
    public void delete(Long productId) {
        repository.deleteById(productId);
    }
}
