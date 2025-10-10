package aforo.productrateplanservice.product.service;

import aforo.productrateplanservice.exception.NotFoundException;
import aforo.productrateplanservice.product.dto.ProductFlatFileDTO;
import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.entity.ProductFlatFile;
import aforo.productrateplanservice.product.enums.ProductType;
import aforo.productrateplanservice.product.mapper.ProductFlatFileMapper;
import aforo.productrateplanservice.product.repository.*;
import aforo.productrateplanservice.product.request.CreateProductFlatFileRequest;
import aforo.productrateplanservice.product.request.UpdateProductFlatFileRequest;
import aforo.productrateplanservice.tenant.TenantContext;
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
    private final ProductSQLResultRepository productSQLResultRepository;
    private final ProductLLMTokenRepository productLLMTokenRepository;
    private final ProductStorageRepository productStorageRepository;
    private final ProductFlatFileMapper mapper;

    @Override
    @Transactional
    public ProductFlatFileDTO create(Long productId, CreateProductFlatFileRequest request) {
        Long orgId = TenantContext.require();
        Product product = productRepository.findByProductIdAndOrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("Product " + productId + " not found"));

        if (flatFileRepository.existsByProduct_ProductId(productId)) {
            throw new IllegalStateException("Product " + productId + " already has FlatFile configuration.");
        }
        if (productAPIRepository.existsByProduct_ProductId(productId)
                || productLLMTokenRepository.existsByProduct_ProductId(productId)
                || productSQLResultRepository.existsByProduct_ProductId(productId)
                || productStorageRepository.existsByProduct_ProductId(productId)) {
            throw new IllegalStateException("Product " + productId + " already has a different configuration type.");
        }
        if (product.getProductType() != null && product.getProductType() != ProductType.FlatFile) {
            throw new IllegalStateException("Product type already set to " + product.getProductType());
        }

        ProductFlatFile entity = ProductFlatFile.builder()
                .product(product)
                .fileLocation(request.getFileLocation())
                .format(request.getFormat())
                .build();

        ProductFlatFileDTO dto = mapper.toDTO(flatFileRepository.save(entity));

        product.setProductType(ProductType.FlatFile);
        productRepository.save(product);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductFlatFileDTO getByProductId(Long productId) {
        Long orgId = TenantContext.require();
        ProductFlatFile entity = flatFileRepository
                .findByProduct_ProductIdAndProduct_OrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("FlatFile configuration not found for product " + productId));
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductFlatFileDTO> getAll() {
        Long orgId = TenantContext.require();
        return flatFileRepository.findAllByProduct_OrganizationId(orgId)
                .stream().map(mapper::toDTO).toList();
    }

    @Override
    @Transactional
    public ProductFlatFileDTO update(Long productId, UpdateProductFlatFileRequest request) {
        Long orgId = TenantContext.require();
        ProductFlatFile existing = flatFileRepository
                .findByProduct_ProductIdAndProduct_OrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("FlatFile configuration not found for product " + productId));

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
        Long orgId = TenantContext.require();
        ProductFlatFile existing = flatFileRepository
                .findByProduct_ProductIdAndProduct_OrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("FlatFile configuration not found for product " + productId));

        if (request.getFileLocation() != null) existing.setFileLocation(request.getFileLocation());
        if (request.getFormat() != null) existing.setFormat(request.getFormat());

        return mapper.toDTO(flatFileRepository.save(existing));
    }

    @Override
    @Transactional
    public void delete(Long productId) {
        Long orgId = TenantContext.require();
        Product product = productRepository.findByProductIdAndOrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("Product " + productId + " not found"));

        flatFileRepository
                .findByProduct_ProductIdAndProduct_OrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("FlatFile configuration not found for product " + productId));
        flatFileRepository.deleteById(productId);

        if (product.getProductType() == ProductType.FlatFile) {
            product.setProductType(null);
            productRepository.save(product);
        }
    }
}
