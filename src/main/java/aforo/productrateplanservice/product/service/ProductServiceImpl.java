package aforo.productrateplanservice.product.service;

import aforo.productrateplanservice.product.dto.ProductDTO;
import aforo.productrateplanservice.product.entity.*;
import aforo.productrateplanservice.product.mapper.ProductMapper;
import aforo.productrateplanservice.product.repository.*;
import aforo.productrateplanservice.product.request.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import aforo.productrateplanservice.exception.NotFoundException;
import aforo.productrateplanservice.product.assembler.ProductAssembler;

import java.util.List;
import java.util.stream.Collectors;
import aforo.productrateplanservice.product.enums.ProductType;
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductAPIRepository productAPIRepository;
    private final ProductFlatFileRepository productFlatFileRepository;
    private final ProductLLMTokenRepository productLLMTokenRepository;
    private final ProductSQLResultRepository productSQLResultRepository;
    private final ProductAssembler productAssembler;

    @Override
    public ProductDTO createProduct(CreateProductRequest request) {
        Product product = productMapper.toEntity(request);
        Product saved = productRepository.save(product);
        return productMapper.toDTO(saved);
    }

    @Override
    public ProductDTO getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        return productMapper.toDTO(product);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    
        // Check and update each field if not null
        if (request.getProductName() != null) product.setProductName(request.getProductName());
        if (request.getProductType() != null && request.getProductType() != product.getProductType()) {
            handleProductTypeChange(product, request.getProductType());
        }
        if (request.getVersion() != null) product.setVersion(request.getVersion());
        if (request.getProductDescription() != null) product.setProductDescription(request.getProductDescription());
        if (request.getTags() != null) product.setTags(request.getTags());
        if (request.getCategory() != null) product.setCategory(request.getCategory());
        if (request.getVisibility() != null) product.setVisibility(request.getVisibility());
        if (request.getStatus() != null) product.setStatus(request.getStatus());
        if (request.getInternalSkuCode() != null) product.setInternalSkuCode(request.getInternalSkuCode());
        if (request.getUom() != null) product.setUom(request.getUom());
        if (request.getEffectiveStartDate() != null) product.setEffectiveStartDate(request.getEffectiveStartDate());
        if (request.getEffectiveEndDate() != null) product.setEffectiveEndDate(request.getEffectiveEndDate());
if (request.getBillable() != null) product.setBillable(request.getBillable());
        if (request.getLinkedRatePlans() != null) product.setLinkedRatePlans(request.getLinkedRatePlans());
        if (request.getLabels() != null) product.setLabels(request.getLabels());
        if (request.getAuditLogId() != null) product.setAuditLogId(request.getAuditLogId());
    
        Product updated = productRepository.save(product);
        return productAssembler.toDTO(updated);
    }
    
    @Override
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found with id: " + productId);
        }
        productRepository.deleteById(productId);
    }
    private void handleProductTypeChange(Product product, ProductType newType) {
        Long productId = product.getProductId();
        // Delete old type if exists
        productAPIRepository.deleteById(productId);
        productFlatFileRepository.deleteById(productId);
        productSQLResultRepository.deleteById(productId);
        productLLMTokenRepository.deleteById(productId);
        // Set new type
        product.setProductType(newType);
    }
    
}