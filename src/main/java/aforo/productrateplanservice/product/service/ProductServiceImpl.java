package aforo.productrateplanservice.product.service;

import aforo.productrateplanservice.exception.NotFoundException;
import aforo.productrateplanservice.product.assembler.ProductAssembler;
import aforo.productrateplanservice.product.dto.ProductDTO;
import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.entity.ProductAPI;
import aforo.productrateplanservice.product.entity.ProductFlatFile;
import aforo.productrateplanservice.product.entity.ProductLLMToken;
import aforo.productrateplanservice.product.entity.ProductSQLResult;
import aforo.productrateplanservice.product.entity.ProductStorage;
import aforo.productrateplanservice.product.enums.ProductStatus;
import aforo.productrateplanservice.product.mapper.ProductMapper;
import aforo.productrateplanservice.product.repository.ProductAPIRepository;
import aforo.productrateplanservice.product.repository.ProductFlatFileRepository;
import aforo.productrateplanservice.product.repository.ProductLLMTokenRepository;
import aforo.productrateplanservice.product.repository.ProductRepository;
import aforo.productrateplanservice.product.repository.ProductSQLResultRepository;
import aforo.productrateplanservice.product.repository.ProductStorageRepository;
import aforo.productrateplanservice.product.request.CreateProductRequest;
import aforo.productrateplanservice.product.request.UpdateProductRequest;
import aforo.productrateplanservice.product.response.ProductImportResponse;
import aforo.productrateplanservice.rate_plan.RatePlanRepository;
import aforo.productrateplanservice.client.BillableMetricClient;
import aforo.productrateplanservice.storage.IconStorageService;
import aforo.productrateplanservice.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductAssembler productAssembler;
    private final RatePlanRepository ratePlanRepository;
    private final BillableMetricClient billableMetricClient;
    private final ProductAPIRepository productAPIRepository;
    private final ProductFlatFileRepository productFlatFileRepository;
    private final ProductSQLResultRepository productSQLResultRepository;
    private final ProductLLMTokenRepository productLLMTokenRepository;
    private final ProductStorageRepository productStorageRepository;
    private final IconStorageService iconStorageService;

    @Override
    @Transactional
    public ProductDTO createProduct(CreateProductRequest request) {
        Long orgId = TenantContext.require();
        // normalize inputs
        String name = trim(request.getProductName());
        String sku  = trim(request.getInternalSkuCode());

        // For drafts, productName may be omitted

        // uniqueness
        if (name != null && productRepository.findByProductNameIgnoreCaseAndOrganizationId(name, orgId).isPresent()) {
            throw new IllegalArgumentException("productName already exists");
        }
        // internalSkuCode is optional for drafts; only validate when provided
        if (sku != null && productRepository.existsByInternalSkuCodeAndOrganizationId(sku, orgId)) {
            throw new IllegalArgumentException("internalSkuCode already exists");
        }

        Product product = productMapper.toEntity(request);
        product.setProductName(name);

        // Source handling: default to MANUAL for manual creation when not provided
        String src = request.getSource();
        if (src == null || src.trim().isEmpty()) {
            src = "MANUAL";
        }
        product.setSource(src.trim().toUpperCase());

        // ExternalId is only meaningful for imported products; ignore if blank
        if (request.getExternalId() != null && !request.getExternalId().trim().isEmpty()) {
            product.setExternalId(request.getExternalId().trim());
        }

        // set only when provided (draft creation may omit it)
        if (sku != null) {
            product.setInternalSkuCode(sku);
        }

        product.setOrganizationId(orgId);

        Product saved = productRepository.save(product);
        return productAssembler.toDTO(saved);
    }

    @Override
    @Transactional
    public ProductDTO createProduct(CreateProductRequest request, MultipartFile icon) {
        ProductDTO dto = createProduct(request);
        if (icon != null && !icon.isEmpty()) {
            dto = updateIcon(dto.getProductId(), icon);
        }
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long productId) {
        Long orgId = TenantContext.require();
        Product product = productRepository.findByProductIdAndOrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));
    
        ProductDTO dto = productAssembler.toDTO(product);
        // fetch only metrics for this product
        dto.setBillableMetrics(billableMetricClient.getMetricsByProductId(productId));
        return dto;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        Long orgId = TenantContext.require();
        return productRepository.findAllByOrganizationId(orgId).stream()
                .map(product -> {
                    ProductDTO dto = productAssembler.toDTO(product);
                    // fetch only metrics linked to each product
                    dto.setBillableMetrics(
                            billableMetricClient.getMetricsByProductId(product.getProductId())
                    );
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public ProductDTO updateProductFully(Long id, UpdateProductRequest request) {
        Long orgId = TenantContext.require();
        Product product = productRepository.findByProductIdAndOrganizationId(id, orgId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        // PUT requires productName & internalSkuCode
        String name = trim(request.getProductName());
        String sku  = trim(request.getInternalSkuCode());
        if (name == null) throw new IllegalArgumentException("productName is required for PUT");
        if (sku  == null) throw new IllegalArgumentException("internalSkuCode is required for PUT");

        // name uniqueness (ignore-case, trim) excluding self
        if (productRepository.existsByProductNameTrimmedIgnoreCaseAndOrganizationId(name, id, orgId)) {
            throw new IllegalArgumentException("productName already exists");
        }
        // sku uniqueness excluding self
        if (!sku.equals(product.getInternalSkuCode()) &&
            productRepository.existsByInternalSkuCodeAndOrganizationId(sku, orgId)) {
            throw new IllegalArgumentException("internalSkuCode already exists");
        }

        product.setProductName(name);
        product.setVersion(request.getVersion());
        product.setProductDescription(request.getProductDescription());
        product.setInternalSkuCode(sku);

        return productAssembler.toDTO(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductDTO updateProductPartially(Long id, UpdateProductRequest request) {
        Long orgId = TenantContext.require();
        Product product = productRepository.findByProductIdAndOrganizationId(id, orgId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (request.getProductName() != null) {
            String name = trim(request.getProductName());
            if (name == null) throw new IllegalArgumentException("productName cannot be blank");
            if (productRepository.existsByProductNameTrimmedIgnoreCaseAndOrganizationId(name, id, orgId)) {
                throw new IllegalArgumentException("productName already exists");
            }
            product.setProductName(name);
        }
        if (request.getVersion() != null) product.setVersion(request.getVersion());
        if (request.getProductDescription() != null) product.setProductDescription(request.getProductDescription());
        if (request.getInternalSkuCode() != null) {
            String sku = trim(request.getInternalSkuCode());
            if (sku == null) throw new IllegalArgumentException("internalSkuCode cannot be blank");
            if (!sku.equals(product.getInternalSkuCode()) &&
                productRepository.existsByInternalSkuCodeAndOrganizationId(sku, orgId)) {
                throw new IllegalArgumentException("internalSkuCode already exists");
            }
            product.setInternalSkuCode(sku);
        }

        return productAssembler.toDTO(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        Long orgId = TenantContext.require();
        // validate existence
        productRepository.findByProductIdAndOrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + productId));

        // delete child rate plans first
        ratePlanRepository.deleteByProduct_ProductIdAndOrganizationId(productId, orgId);

        // delete billable metrics linked to this product in external service
        billableMetricClient.deleteMetricsByProductId(productId);

        productRepository.deleteByProductIdAndOrganizationId(productId, orgId);
    }

    private static String trim(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    @Override
    @Transactional
    public ProductDTO finalizeProduct(Long id) {
        Long orgId = TenantContext.require();
        Product product = productRepository.findByProductIdAndOrganizationId(id, orgId)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));

        // Only DRAFT can be finalized
        if (product.getStatus() != ProductStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT products can be finalized.");
        }

        // 1) Product-level required fields
        requireNonBlank(product.getProductName(), "productName");
        requireNonBlank(product.getInternalSkuCode(), "internalSkuCode");

        // Uniqueness (exclude self)
        String normalizedName = product.getProductName().trim();
        if (productRepository.existsByProductNameTrimmedIgnoreCaseAndOrganizationId(normalizedName, id, orgId)) {
            throw new IllegalArgumentException("productName already exists");
        }

        // 2) Exactly one complete type config
        int validTypeCount = 0;

        // API
        Optional<ProductAPI> apiOpt = productAPIRepository.findById(id);
        if (apiOpt.isPresent()) {
            ProductAPI api = apiOpt.get();
            if (isBlank(api.getEndpointUrl()) || api.getAuthType() == null) {
                throw new IllegalArgumentException("Complete API config required: endpointUrl, authType.");
            }
            validTypeCount++;
        }

        // FlatFile
        Optional<ProductFlatFile> flatOpt = productFlatFileRepository.findById(id);
        if (flatOpt.isPresent()) {
            ProductFlatFile ff = flatOpt.get();
            // For DRAFT you allowed nulls; for FINALIZE require both
            if (isBlank(ff.getFileLocation()) || ff.getFormat() == null) {
                throw new IllegalArgumentException("Complete FlatFile config required: fileLocation, format.");
            }
            validTypeCount++;
        }

        // SQL Result
        Optional<ProductSQLResult> sqlOpt = productSQLResultRepository.findById(id);
        if (sqlOpt.isPresent()) {
            ProductSQLResult sql = sqlOpt.get();
            if (isBlank(sql.getConnectionString()) || sql.getDbType() == null || sql.getAuthType() == null) {
                throw new IllegalArgumentException("Complete SQLResult config required: connectionString, dbType, authType.");
            }
            validTypeCount++;
        }

        // LLM Token
        Optional<ProductLLMToken> llmOpt = productLLMTokenRepository.findById(id);
        if (llmOpt.isPresent()) {
            ProductLLMToken llm = llmOpt.get();
            if (isBlank(llm.getModelName()) || isBlank(llm.getEndpointUrl()) || llm.getAuthType() == null) {
                throw new IllegalArgumentException("Complete LLMToken config required: modelName, endpointUrl, authType.");
            }
            validTypeCount++;
        }

        // Storage
        Optional<ProductStorage> storageOpt = productStorageRepository.findById(id);
        if (storageOpt.isPresent()) {
            ProductStorage st = storageOpt.get();
            if (isBlank(st.getStorageLocation()) || st.getAuthType() == null) {
                throw new IllegalArgumentException("Complete Storage config required: storageLocation, authType.");
            }
            validTypeCount++;
        }

        if (validTypeCount == 0) {
            throw new IllegalArgumentException("At least one product type configuration must be created and complete.");
        }
        if (validTypeCount > 1) {
            throw new IllegalArgumentException("Multiple type configurations found. Only one product type is allowed per product.");
        }

        // All good → activate
        product.setStatus(ProductStatus.ACTIVE);
        Product saved = productRepository.save(product);
        return productAssembler.toDTO(saved);
    }

    // ---- helpers ----
    private static void requireNonBlank(String value, String fieldName) {
        if (isBlank(value)) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    @Override
    @Transactional(readOnly = true)
    public String getIconUrl(Long id) {
        Long orgId = TenantContext.require();
        Product product = productRepository.findByProductIdAndOrganizationId(id, orgId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        String icon = product.getIcon();
        if (icon == null || icon.trim().isEmpty()) {
            throw new NotFoundException("Icon not found for product id: " + id);
        }
        return icon;
    }

    @Override
    @Transactional
    public ProductDTO updateIcon(Long id, MultipartFile icon) {
        if (icon == null || icon.isEmpty()) {
            throw new IllegalArgumentException("Icon file is required");
        }
        Long orgId = TenantContext.require();
        Product product = productRepository.findByProductIdAndOrganizationId(id, orgId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        // delete previous file if any
        if (product.getIcon() != null) {
            iconStorageService.deleteByUrl(product.getIcon());
        }

        String url = iconStorageService.saveIcon(icon, id);
        product.setIcon(url);
        Product saved = productRepository.save(product);
        return productAssembler.toDTO(saved);
    }

    @Override
    @Transactional
    public void deleteIcon(Long id) {
        Long orgId = TenantContext.require();
        Product product = productRepository.findByProductIdAndOrganizationId(id, orgId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        if (product.getIcon() != null) {
            iconStorageService.deleteByUrl(product.getIcon());
            product.setIcon(null);
            productRepository.save(product);
        }
    }

    @Override
    @Transactional
    public void clearProductTypeConfiguration(Long productId) {
        Long orgId = TenantContext.require();
        Product product = productRepository.findByProductIdAndOrganizationId(productId, orgId)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));

        // Delete all possible product type configurations
        // This allows the user to switch to a different product type
        productAPIRepository.deleteById(productId);
        productFlatFileRepository.deleteById(productId);
        productSQLResultRepository.deleteById(productId);
        productLLMTokenRepository.deleteById(productId);
        productStorageRepository.deleteById(productId);
        
        // Clear the cached product type in the Product entity
        product.setProductType(null);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public ProductImportResponse importExternalProduct(CreateProductRequest request) {
        Long orgId = TenantContext.require();
        
        // Validate required fields for import
        if (request.getExternalId() == null || request.getExternalId().trim().isEmpty()) {
            throw new IllegalArgumentException("External ID is required for product import");
        }
        if (request.getSource() == null || request.getSource().trim().isEmpty()) {
            throw new IllegalArgumentException("Source is required for product import");
        }
        if (request.getProductName() == null || request.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required for product import");
        }
        
        String externalId = request.getExternalId().trim();
        String source = request.getSource().trim().toUpperCase();
        String productName = request.getProductName().trim();
        
        // Validate source - must be KONG or APIGEE
        if (!"KONG".equals(source) && !"APIGEE".equals(source)) {
            throw new IllegalArgumentException("Invalid source. Must be 'KONG' or 'APIGEE'");
        }
        
        log.info("Importing product [{}] from source [{}] with externalId [{}]", productName, source, externalId);
        
        // Check if a product with the same externalId and source already exists
        Optional<Product> existingProduct = productRepository.findByExternalIdAndSourceAndOrganizationId(
            externalId, source, orgId
        );
        
        Product product;
        String status;
        
        if (existingProduct.isPresent()) {
            // Update existing product
            product = existingProduct.get();
            product.setProductName(productName);
            product.setProductDescription(request.getProductDescription());
            product.setVersion(request.getVersion());
            // Keep source, externalId, and productType as is
            status = "UPDATED";
            log.info("Updated existing product with ID [{}] from source [{}]", product.getProductId(), source);
        } else {
            // Create new product
            product = productMapper.toEntity(request);
            product.setOrganizationId(orgId);
            product.setSource(source);
            product.setExternalId(externalId);
            product.setProductName(productName);
            
            // Auto-set ProductType to API for all imported products
            product.setProductType(aforo.productrateplanservice.product.enums.ProductType.API);
            
            // Generate SKU if not provided
            if (request.getInternalSkuCode() == null || request.getInternalSkuCode().trim().isEmpty()) {
                // Format: KONG-{externalId} or APIGEE-{externalId}
                product.setInternalSkuCode(source + "-" + externalId);
            } else {
                product.setInternalSkuCode(request.getInternalSkuCode().trim());
            }
            
            // Set default status to DRAFT (can be finalized later)
            product.setStatus(aforo.productrateplanservice.product.enums.ProductStatus.DRAFT);
            
            status = "CREATED";
            log.info("Created new product from source [{}] with externalId [{}], ProductType set to API", source, externalId);
        }
        
        Product savedProduct = productRepository.save(product);
        log.info("Imported product [{}] from source [{}] with ProductType: {}", 
                savedProduct.getProductName(), savedProduct.getSource(), savedProduct.getProductType());
        
        return ProductImportResponse.builder()
            .message("Product imported successfully from " + source)
            .status(status)
            .productId(savedProduct.getProductId())
            .productName(savedProduct.getProductName())
            .source(savedProduct.getSource())
            .externalId(savedProduct.getExternalId())
            .build();
    }

}
