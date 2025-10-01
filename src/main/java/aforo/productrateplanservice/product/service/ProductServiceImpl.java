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
import aforo.productrateplanservice.rate_plan.RatePlanRepository;
import aforo.productrateplanservice.client.BillableMetricClient;
import aforo.productrateplanservice.product.status.ProductStatusResolver;
import aforo.productrateplanservice.storage.IconStorageService;
import aforo.productrateplanservice.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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
    private final ProductStatusResolver productStatusResolver;

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
        // compute derived status
        dto.setStatus(productStatusResolver.compute(product));
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
                    // compute derived status
                    dto.setStatus(productStatusResolver.compute(product));
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

        // All good → validations passed; do not mutate status field (status is derived)
        // Move lifecycle from DRAFT -> CONFIGURED on explicit finalize
        product.setStatus(ProductStatus.CONFIGURED);
        Product saved = productRepository.save(product);
        ProductDTO dto = productAssembler.toDTO(saved);
        dto.setStatus(productStatusResolver.compute(saved));
        return dto;
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

}
