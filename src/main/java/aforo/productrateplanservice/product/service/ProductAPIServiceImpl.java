package aforo.productrateplanservice.product.service;

import aforo.productrateplanservice.exception.NotFoundException;
import aforo.productrateplanservice.product.dto.ProductAPIDTO;
import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.entity.ProductAPI;
import aforo.productrateplanservice.product.enums.ProductType;
import aforo.productrateplanservice.product.mapper.ProductAPIMapper;
import aforo.productrateplanservice.product.repository.*;
import aforo.productrateplanservice.product.request.CreateProductAPIRequest;
import aforo.productrateplanservice.product.request.UpdateProductAPIRequest;
import aforo.productrateplanservice.tenant.TenantContext;
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

    private final ProductFlatFileRepository productFlatFileRepository;
    private final ProductSQLResultRepository productSQLResultRepository;
    private final ProductLLMTokenRepository productLLMTokenRepository;
    private final ProductStorageRepository productStorageRepository;

    @Override
    @Transactional
    public ProductAPIDTO create(Long productId, CreateProductAPIRequest request) {
        Long orgId = TenantContext.require();
        Product product = productRepository.findByProductIdAndOrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("Product " + productId + " not found"));

        // enforce exactly one type per product
        if (productAPIRepository.existsByProduct_ProductId(productId)) {
            throw new IllegalStateException("Product " + productId + " already has API configuration.");
        }
        if (productFlatFileRepository.existsByProduct_ProductId(productId)
                || productSQLResultRepository.existsByProduct_ProductId(productId)
                || productLLMTokenRepository.existsByProduct_ProductId(productId)
                || productStorageRepository.existsByProduct_ProductId(productId)) {
            throw new IllegalStateException(
                    "Product " + productId + " already has a different configuration type.");
        }
        // also guard against stale productType
        if (product.getProductType() != null && product.getProductType() != ProductType.API) {
            throw new IllegalStateException("Product type already set to " + product.getProductType());
        }

        ProductAPI productAPI = ProductAPI.builder()
                .product(product)
                .endpointUrl(request.getEndpointUrl())
                .authType(request.getAuthType())
                .build();

        ProductAPIDTO dto = productAPIMapper.toDTO(productAPIRepository.save(productAPI));

        // sync the type on the parent
        product.setProductType(ProductType.API);
        productRepository.save(product);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductAPIDTO getByProductId(Long productId) {
        Long orgId = TenantContext.require();
        ProductAPI api = productAPIRepository
                .findByProduct_ProductIdAndProduct_OrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("API configuration not found for product " + productId));
        return productAPIMapper.toDTO(api);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductAPIDTO> getAll() {
        Long orgId = TenantContext.require();
        return productAPIRepository.findAllByProduct_OrganizationId(orgId)
                .stream()
                .map(productAPIMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public ProductAPIDTO updateFully(Long productId, UpdateProductAPIRequest request) {
        Long orgId = TenantContext.require();
        ProductAPI existing = productAPIRepository
                .findByProduct_ProductIdAndProduct_OrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("API configuration not found for product " + productId));

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
        Long orgId = TenantContext.require();
        ProductAPI existing = productAPIRepository
                .findByProduct_ProductIdAndProduct_OrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("API configuration not found for product " + productId));

        if (request.getEndpointUrl() != null) existing.setEndpointUrl(request.getEndpointUrl());
        if (request.getAuthType() != null) existing.setAuthType(request.getAuthType());

        return productAPIMapper.toDTO(productAPIRepository.save(existing));
    }

    @Override
    @Transactional
    public void delete(Long productId) {
        Long orgId = TenantContext.require();
        Product product = productRepository.findByProductIdAndOrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("Product " + productId + " not found"));

        productAPIRepository
                .findByProduct_ProductIdAndProduct_OrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("API configuration not found for product " + productId));

        productAPIRepository.deleteById(productId);

        // clear productType if it points to API
        if (product.getProductType() == ProductType.API) {
            product.setProductType(null);
            productRepository.save(product);
        }
    }
}
