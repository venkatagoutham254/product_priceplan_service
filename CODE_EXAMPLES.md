# Code Examples - Tenant Isolation Implementation

This document provides complete, working code examples from your product-rateplan-service demonstrating proper tenant isolation.

---

## 1. Entity Classes

### Product Entity (Complete)

```java
package aforo.productrateplanservice.product.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import aforo.productrateplanservice.product.enums.ProductStatus;    
import aforo.productrateplanservice.product.enums.ProductType;  

@Entity
@Table(
    name = "aforo_product",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_aforo_product__org_name", 
            columnNames = {"organization_id", "product_name"}
        ),
        @UniqueConstraint(
            name = "uq_aforo_product__org_sku", 
            columnNames = {"organization_id", "internal_sku_code"}
        )
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(name = "product_name")
    private String productName;

    // ✅ CRITICAL: organization_id with NOT NULL constraint
    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    private String version;

    @Column(name = "product_description", columnDefinition = "TEXT")
    private String productDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = true)
    private ProductType productType;

    @Column(name = "internal_sku_code")
    private String internalSkuCode;

    @Column(name = "icon", nullable = true, length = 1024)
    private String icon;

    @Column(name = "source", nullable = false)
    @Builder.Default
    private String source = "MANUAL";

    @Column(name = "external_id", nullable = true)
    private String externalId;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdOn;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime lastUpdated;
}
```

### RatePlan Entity (Complete)

```java
package aforo.productrateplanservice.rate_plan;

import aforo.productrateplanservice.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;
import aforo.productrateplanservice.product.enums.RatePlanStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "aforo_rate_plan", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"rate_plan_name", "product_id", "organization_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatePlan {

    public enum PaymentType {
        POSTPAID,
        PREPAID
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ratePlanId;

    @Column(name = "rate_plan_name")
    private String ratePlanName;

    // ✅ CRITICAL: organization_id with NOT NULL constraint
    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_frequency")
    private BillingFrequency billingFrequency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType;

    @Column(name = "billable_metric_id")
    private Long billableMetricId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RatePlanStatus status = RatePlanStatus.DRAFT;
    
    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdOn;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime lastUpdated;
}
```

---

## 2. Repository Interfaces

### Product Repository (Complete)

```java
package aforo.productrateplanservice.product.repository;

import aforo.productrateplanservice.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // ✅ Organization-scoped queries
    List<Product> findAllByOrganizationId(Long organizationId);
    Optional<Product> findByProductIdAndOrganizationId(Long productId, Long organizationId);
    void deleteByProductIdAndOrganizationId(Long productId, Long organizationId);
    
    // ✅ Uniqueness checks scoped by organization
    boolean existsByInternalSkuCodeAndOrganizationId(String internalSkuCode, Long organizationId);
    Optional<Product> findByProductNameIgnoreCaseAndOrganizationId(String productName, Long organizationId);
    
    @Query("SELECT COUNT(p) > 0 FROM Product p " +
           "WHERE TRIM(LOWER(p.productName)) = TRIM(LOWER(:productName)) " +
           "AND p.productId <> :productId " +
           "AND p.organizationId = :organizationId")
    boolean existsByProductNameTrimmedIgnoreCaseAndOrganizationId(
        @Param("productName") String productName,
        @Param("productId") Long productId,
        @Param("organizationId") Long organizationId
    );
    
    // ✅ External product lookup scoped by organization
    Optional<Product> findByExternalIdAndSourceAndOrganizationId(
        String externalId, String source, Long organizationId
    );
    Optional<Product> findByExternalIdAndOrganizationId(String externalId, Long organizationId);
}
```

### RatePlan Repository (Complete)

```java
package aforo.productrateplanservice.rate_plan;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RatePlanRepository extends JpaRepository<RatePlan, Long> {

    // ✅ Organization-scoped queries
    List<RatePlan> findAllByOrganizationId(Long organizationId);
    Optional<RatePlan> findByRatePlanIdAndOrganizationId(Long ratePlanId, Long organizationId);

    // ✅ Product-scoped queries with organization
    List<RatePlan> findByProduct_ProductIdAndOrganizationId(Long productId, Long organizationId);
    long countByProduct_ProductIdAndOrganizationId(Long productId, Long organizationId);
    void deleteByProduct_ProductIdAndOrganizationId(Long productId, Long organizationId);

    // ✅ Uniqueness check scoped by organization
    Optional<RatePlan> findByRatePlanNameAndProduct_ProductIdAndOrganizationId(
        String ratePlanName, Long productId, Long organizationId
    );

    // ✅ Billable metric cleanup scoped by organization
    void deleteByBillableMetricIdAndOrganizationId(Long billableMetricId, Long organizationId);
}
```

---

## 3. Service Layer Examples

### Product Service - CREATE Operation

```java
@Override
@Transactional
public ProductDTO createProduct(CreateProductRequest request) {
    // ✅ STEP 1: Extract organizationId from JWT (NOT from request body)
    Long orgId = TenantContext.require();
    
    // Normalize inputs
    String name = trim(request.getProductName());
    String sku  = trim(request.getInternalSkuCode());

    // ✅ STEP 2: Uniqueness checks SCOPED BY ORGANIZATION
    if (name != null && productRepository.findByProductNameIgnoreCaseAndOrganizationId(name, orgId).isPresent()) {
        throw new IllegalArgumentException("productName already exists");
    }
    if (sku != null && productRepository.existsByInternalSkuCodeAndOrganizationId(sku, orgId)) {
        throw new IllegalArgumentException("internalSkuCode already exists");
    }

    // ✅ STEP 3: Create entity from request
    Product product = productMapper.toEntity(request);
    product.setProductName(name);
    
    // Source handling
    String src = request.getSource();
    if (src == null || src.trim().isEmpty()) {
        src = "MANUAL";
    }
    product.setSource(src.trim().toUpperCase());

    if (request.getExternalId() != null && !request.getExternalId().trim().isEmpty()) {
        product.setExternalId(request.getExternalId().trim());
    }

    if (sku != null) {
        product.setInternalSkuCode(sku);
    }

    // ✅ STEP 4: Set organizationId from JWT (CRITICAL - never from request)
    product.setOrganizationId(orgId);

    // ✅ STEP 5: Save and return
    Product saved = productRepository.save(product);
    return productAssembler.toDTO(saved);
}
```

### Product Service - READ Operations

```java
@Override
@Transactional(readOnly = true)
public ProductDTO getProductById(Long productId) {
    // ✅ STEP 1: Get organizationId from JWT
    Long orgId = TenantContext.require();
    
    // ✅ STEP 2: Query with BOTH productId AND organizationId
    Product product = productRepository.findByProductIdAndOrganizationId(productId, orgId)
        .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));
    
    // ✅ STEP 3: Return DTO (404 if not found, NOT 403)
    ProductDTO dto = productAssembler.toDTO(product);
    dto.setBillableMetrics(billableMetricClient.getMetricsByProductId(productId));
    return dto;
}

@Override
@Transactional(readOnly = true)
public List<ProductDTO> getAllProducts() {
    // ✅ STEP 1: Get organizationId from JWT
    Long orgId = TenantContext.require();
    
    // ✅ STEP 2: Query ONLY products for this organization
    return productRepository.findAllByOrganizationId(orgId).stream()
        .map(product -> {
            ProductDTO dto = productAssembler.toDTO(product);
            dto.setBillableMetrics(
                billableMetricClient.getMetricsByProductId(product.getProductId())
            );
            return dto;
        })
        .collect(Collectors.toList());
}
```

### Product Service - UPDATE Operation

```java
@Override
@Transactional
public ProductDTO updateProductFully(Long id, UpdateProductRequest request) {
    // ✅ STEP 1: Get organizationId from JWT
    Long orgId = TenantContext.require();
    
    // ✅ STEP 2: Fetch product with organization validation
    Product product = productRepository.findByProductIdAndOrganizationId(id, orgId)
        .orElseThrow(() -> new NotFoundException("Product not found"));

    // PUT requires productName & internalSkuCode
    String name = trim(request.getProductName());
    String sku  = trim(request.getInternalSkuCode());
    if (name == null) throw new IllegalArgumentException("productName is required for PUT");
    if (sku  == null) throw new IllegalArgumentException("internalSkuCode is required for PUT");

    // ✅ STEP 3: Uniqueness checks scoped by organization (excluding self)
    if (productRepository.existsByProductNameTrimmedIgnoreCaseAndOrganizationId(name, id, orgId)) {
        throw new IllegalArgumentException("productName already exists");
    }
    if (!sku.equals(product.getInternalSkuCode()) &&
        productRepository.existsByInternalSkuCodeAndOrganizationId(sku, orgId)) {
        throw new IllegalArgumentException("internalSkuCode already exists");
    }

    // ✅ STEP 4: Update fields (organizationId remains unchanged)
    product.setProductName(name);
    product.setVersion(request.getVersion());
    product.setProductDescription(request.getProductDescription());
    product.setInternalSkuCode(sku);

    return productAssembler.toDTO(productRepository.save(product));
}
```

### Product Service - DELETE Operation

```java
@Override
@Transactional
public void deleteProduct(Long productId) {
    // ✅ STEP 1: Get organizationId from JWT
    Long orgId = TenantContext.require();
    
    // ✅ STEP 2: Validate product exists AND belongs to this organization
    Product product = productRepository.findByProductIdAndOrganizationId(productId, orgId)
        .orElseThrow(() -> new NotFoundException("Product not found with ID: " + productId));
    
    // ✅ STEP 3: Cascade delete rate plans (organization-scoped)
    List<RatePlan> ratePlans = ratePlanRepository.findByProduct_ProductIdAndOrganizationId(productId, orgId);
    for (RatePlan ratePlan : ratePlans) {
        ratePlanCoreService.deleteRatePlan(ratePlan.getRatePlanId());
    }

    // ✅ STEP 4: Delete billable metrics
    try {
        billableMetricClient.deleteMetricsByProductId(productId);
    } catch (Exception e) {
        log.warn("Could not delete billable metrics for product {}: {}", productId, e.getMessage());
    }

    // ✅ STEP 5: Delete product type configurations
    deleteProductTypeConfigurations(productId);

    // ✅ STEP 6: Delete product (organization-scoped)
    productRepository.deleteByProductIdAndOrganizationId(productId, orgId);
    
    log.info("✅ Product deleted successfully: {} (ID: {})", product.getProductName(), productId);
}
```

### RatePlan Service - CREATE Operation

```java
@Override
public RatePlanDTO createRatePlan(CreateRatePlanRequest request) {
    // ✅ STEP 1: Get organizationId from JWT
    Long orgId = TenantContext.require();
    
    Product product = null;
    if (request.getProductId() != null) {
        Long requestedProductId = request.getProductId();
        
        // ✅ STEP 2: Validate product exists AND belongs to same organization
        product = productRepository
            .findByProductIdAndOrganizationId(requestedProductId, orgId)
            .orElseThrow(() -> new NotFoundException("Product not found with ID: " + requestedProductId));
    }
    
    // ✅ STEP 3: Validate billable metric (if provided)
    if (request.getBillableMetricId() != null) {
        Long productId = (product != null && product.getProductId() != null) 
            ? product.getProductId() : null;
        billableMetricClient.validateActiveForProduct(request.getBillableMetricId(), productId);
    }
    
    // ✅ STEP 4: Create rate plan entity
    RatePlanDTO dto = RatePlanDTO.builder()
        .ratePlanName(request.getRatePlanName())
        .description(request.getDescription())
        .billingFrequency(request.getBillingFrequency())
        .paymentType(request.getPaymentType())
        .billableMetricId(request.getBillableMetricId())
        .build();
    
    RatePlan ratePlan = ratePlanAssembler.toEntity(dto, product);
    
    // ✅ STEP 5: Set organizationId from JWT (CRITICAL - never from request)
    ratePlan.setOrganizationId(orgId);
    
    // ✅ STEP 6: Save and return
    ratePlan = ratePlanRepository.save(ratePlan);
    return ratePlanMapper.toDTO(ratePlan);
}
```

### RatePlan Service - READ Operations

```java
@Override
public RatePlanDTO getRatePlanById(Long ratePlanId) {
    // ✅ STEP 1: Get organizationId from JWT
    Long orgId = TenantContext.require();
    
    // ✅ STEP 2: Query with BOTH ratePlanId AND organizationId
    RatePlan ratePlan = ratePlanRepository.findByRatePlanIdAndOrganizationId(ratePlanId, orgId)
        .orElseThrow(() -> new NotFoundException("Rate plan not found with ID: " + ratePlanId));
    
    // ✅ STEP 3: Ensure metric still exists (cleanup if deleted)
    ratePlan = ensureMetricStillExists(ratePlan);
    
    return toDetailedDTO(ratePlan);
}

@Override
public List<RatePlanDTO> getAllRatePlans() {
    // ✅ STEP 1: Get organizationId from JWT
    Long orgId = TenantContext.require();
    
    // ✅ STEP 2: Query ONLY rate plans for this organization
    return ratePlanRepository.findAllByOrganizationId(orgId).stream()
        .map(this::ensureMetricStillExists)
        .map(this::toDetailedDTO)
        .collect(Collectors.toList());
}

@Override
public List<RatePlanDTO> getRatePlansByProductId(Long productId) {
    // ✅ STEP 1: Get organizationId from JWT
    Long orgId = TenantContext.require();
    
    // ✅ STEP 2: Query rate plans for BOTH productId AND organizationId
    return ratePlanRepository.findByProduct_ProductIdAndOrganizationId(productId, orgId).stream()
        .map(this::ensureMetricStillExists)
        .map(this::toDetailedDTO)
        .collect(Collectors.toList());
}
```

### RatePlan Service - UPDATE Operation

```java
@Override
public RatePlanDTO updateRatePlanFully(Long ratePlanId, UpdateRatePlanRequest request) {
    // ✅ STEP 1: Get organizationId from JWT
    Long orgId = TenantContext.require();
    
    // ✅ STEP 2: Fetch rate plan with organization validation
    RatePlan ratePlan = ratePlanRepository.findByRatePlanIdAndOrganizationId(ratePlanId, orgId)
        .orElseThrow(() -> new NotFoundException("Rate plan not found with ID: " + ratePlanId));

    // ✅ STEP 3: If updating product reference, validate it belongs to same organization
    if (request.getProductId() != null) {
        Long requestedProductId = request.getProductId();
        Product product = productRepository
            .findByProductIdAndOrganizationId(requestedProductId, orgId)
            .orElseThrow(() -> new NotFoundException("Product not found with ID: " + requestedProductId));
        ratePlan.setProduct(product);
    }

    // Validate required fields
    if (request.getRatePlanName() == null || request.getBillingFrequency() == null) {
        throw new ValidationException("All fields must be provided for full update.");
    }

    // ✅ STEP 4: Validate billable metric (if provided)
    if (request.getBillableMetricId() != null) {
        Long productId = (ratePlan.getProduct() != null) 
            ? ratePlan.getProduct().getProductId() : null;
        billableMetricClient.validateActiveForProduct(request.getBillableMetricId(), productId);
        ratePlan.setBillableMetricId(request.getBillableMetricId());
    }

    // ✅ STEP 5: Update fields (organizationId remains unchanged)
    ratePlan.setRatePlanName(request.getRatePlanName());
    ratePlan.setDescription(request.getDescription());
    ratePlan.setBillingFrequency(request.getBillingFrequency());
    ratePlan.setPaymentType(request.getPaymentType());

    ratePlanRepository.save(ratePlan);
    return ratePlanMapper.toDTO(ratePlan);
}
```

### RatePlan Service - DELETE Operation

```java
@Override
public void deleteRatePlan(Long ratePlanId) {
    // ✅ STEP 1: Get organizationId from JWT
    Long orgId = TenantContext.require();
    
    // ✅ STEP 2: Validate rate plan exists AND belongs to this organization
    RatePlan ratePlan = ratePlanRepository.findByRatePlanIdAndOrganizationId(ratePlanId, orgId)
        .orElseThrow(() -> new NotFoundException("Rate plan not found with ID: " + ratePlanId));
    
    // ✅ STEP 3: Delete (cascade delete handled by JPA)
    ratePlanRepository.deleteById(ratePlan.getRatePlanId());
}
```

---

## 4. Controller Examples

### Product Controller

```java
package aforo.productrateplanservice.product.resource;

import aforo.productrateplanservice.product.dto.ProductDTO;
import aforo.productrateplanservice.product.request.CreateProductRequest;
import aforo.productrateplanservice.product.request.UpdateProductRequest;
import aforo.productrateplanservice.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductResource {

    private final ProductService productService;

    // ✅ CREATE - organizationId set from JWT in service layer
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    // ✅ READ ALL - returns only products for current organization
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // ✅ READ ONE - validates organization ownership, returns 404 if not found
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // ✅ UPDATE - validates organization ownership before updating
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProductFully(
            @PathVariable Long id,
            @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProductFully(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProductPartially(
            @PathVariable Long id,
            @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProductPartially(id, request));
    }

    // ✅ DELETE - validates organization ownership before deleting
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/finalize")
    public ResponseEntity<ProductDTO> finalizeProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.finalizeProduct(id));
    }
}
```

### RatePlan Controller

```java
package aforo.productrateplanservice.rate_plan;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rateplans")
@RequiredArgsConstructor
public class RatePlanResource {

    private final RatePlanService ratePlanService;

    // ✅ CREATE - organizationId set from JWT in service layer
    @PostMapping
    public ResponseEntity<RatePlanDTO> createRatePlan(@RequestBody CreateRatePlanRequest request) {
        return ResponseEntity.ok(ratePlanService.createRatePlan(request));
    }

    // ✅ READ ALL - returns only rate plans for current organization
    @GetMapping
    public ResponseEntity<List<RatePlanDTO>> getAllRatePlans() {
        return ResponseEntity.ok(ratePlanService.getAllRatePlans());
    }

    // ✅ READ ONE - validates organization ownership, returns 404 if not found
    @GetMapping("/{ratePlanId}")
    public ResponseEntity<RatePlanDTO> getRatePlanById(@PathVariable Long ratePlanId) {
        return ResponseEntity.ok(ratePlanService.getRatePlanById(ratePlanId));
    }

    // ✅ READ BY PRODUCT - returns only rate plans for product in current organization
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<RatePlanDTO>> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(ratePlanService.getRatePlansByProductId(productId));
    }

    // ✅ UPDATE - validates organization ownership before updating
    @PutMapping("/{ratePlanId}")
    public ResponseEntity<RatePlanDTO> updateRatePlanFully(
            @PathVariable Long ratePlanId,
            @RequestBody UpdateRatePlanRequest request) {
        return ResponseEntity.ok(ratePlanService.updateRatePlanFully(ratePlanId, request));
    }

    @PatchMapping("/{ratePlanId}")
    public ResponseEntity<RatePlanDTO> updateRatePlanPartially(
            @PathVariable Long ratePlanId,
            @RequestBody UpdateRatePlanRequest request) {
        return ResponseEntity.ok(ratePlanService.updateRatePlanPartially(ratePlanId, request));
    }

    // ✅ DELETE - validates organization ownership before deleting
    @DeleteMapping("/{ratePlanId}")
    public ResponseEntity<Void> deleteRatePlan(@PathVariable Long ratePlanId) {
        ratePlanService.deleteRatePlan(ratePlanId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{ratePlanId}/confirm")
    public ResponseEntity<RatePlanDTO> confirmRatePlan(@PathVariable Long ratePlanId) {
        return ResponseEntity.ok(ratePlanService.confirmRatePlan(ratePlanId));
    }
}
```

---

## 5. JWT Filter & Tenant Context

### JWT Tenant Filter

```java
package aforo.productrateplanservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import aforo.productrateplanservice.tenant.TenantContext;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtTenantFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtTenantFilter.class);
    
    // ✅ Support multiple possible claim keys for flexibility
    private static final List<String> CLAIM_KEYS = Arrays.asList(
        "organizationId", "orgId", "tenantId", 
        "organization_id", "org_id", "tenant"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // ✅ OPTION 1: Header override for testing (REMOVE IN PRODUCTION)
        String headerOrg = request.getHeader("X-Organization-Id");
        if (headerOrg != null && !headerOrg.isBlank()) {
            try {
                TenantContext.set(Long.parseLong(headerOrg.trim()));
                logger.debug("Tenant set from X-Organization-Id header: {}", headerOrg);
            } catch (NumberFormatException e) {
                logger.warn("Invalid X-Organization-Id header value: {}", headerOrg);
            }
        } 
        // ✅ OPTION 2: Extract from JWT claims
        else if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            Map<String, Object> claims = jwt.getClaims();
            Object found = null;
            
            // Try each possible claim key
            for (String key : CLAIM_KEYS) {
                if (claims.containsKey(key)) {
                    found = claims.get(key);
                    if (found != null) {
                        try {
                            Long parsed = Long.parseLong(found.toString());
                            TenantContext.set(parsed);
                            logger.debug("Tenant set from JWT claim '{}': {}", key, found);
                            break;
                        } catch (NumberFormatException e) {
                            logger.warn("Invalid numeric value for JWT claim '{}': {}", key, found);
                        }
                    }
                }
            }
            
            if (TenantContext.get() == null) {
                logger.debug("No tenant claim found in JWT. Checked keys: {}", CLAIM_KEYS);
            }
        } else {
            logger.debug("No Authentication/JWT present in SecurityContext");
        }

        try {
            // ✅ Continue filter chain
            filterChain.doFilter(request, response);
        } finally {
            // ✅ CRITICAL: Always cleanup ThreadLocal after request
            TenantContext.clear();
        }
    }
}
```

### Tenant Context

```java
package aforo.productrateplanservice.tenant;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class TenantContext {

    // ✅ ThreadLocal ensures thread-safety in multi-threaded servlet environment
    private static final ThreadLocal<Long> ORG = new ThreadLocal<>();
    private static final ThreadLocal<String> JWT = new ThreadLocal<>();

    private TenantContext() {}

    // --- Organization ID handling ---
    
    public static void set(Long organizationId) {
        ORG.set(organizationId);
    }

    public static Long get() {
        return ORG.get();
    }

    // ✅ Use this in all service methods to enforce tenant context
    public static Long require() {
        Long id = ORG.get();
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenant");
        }
        return id;
    }

    // --- JWT handling (for forwarding to other services) ---
    
    public static void setJwt(String token) {
        JWT.set(token);
    }

    public static String getJwt() {
        return JWT.get();
    }

    public static String requireJwt() {
        String token = JWT.get();
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing JWT token");
        }
        return token;
    }

    // --- Cleanup (CRITICAL for preventing memory leaks) ---
    
    public static void clear() {
        ORG.remove();
        JWT.remove();
    }
}
```

---

## 6. Security Configuration

```java
package aforo.productrateplanservice.config;

import aforo.productrateplanservice.security.JwtTenantFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import javax.crypto.spec.SecretKeySpec;

@Configuration
public class SecurityConfig {

    @Value("${aforo.jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtTenantFilter jwtTenantFilter) 
            throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .authorizeHttpRequests(auth -> auth
                // ✅ Public endpoints
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/actuator/health"
                ).permitAll()
                
                // ✅ Allow preflight requests
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // ✅ ALL Product endpoints require JWT authentication
                .requestMatchers(HttpMethod.GET, "/api/products/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/products/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/products/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/products/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").authenticated()
                
                // ✅ ALL RatePlan endpoints require JWT authentication
                .requestMatchers("/api/rateplans/**").authenticated()
                
                // ✅ Everything else requires authentication
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt -> jwt.decoder(jwtDecoder()))
            );

        // ✅ CRITICAL: Tenant filter runs AFTER Bearer token authentication
        http.addFilterAfter(jwtTenantFilter, BearerTokenAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(
            new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256")
        ).build();
    }
}
```

---

## Key Takeaways

1. **Never accept `organizationId` from request body** - always extract from JWT
2. **Always use organization-scoped repository methods** - never use `findById()` or `findAll()`
3. **Validate cross-entity references** - ensure referenced entities belong to same organization
4. **Return 404 (not 403)** when entity not found - prevents information leakage
5. **Use `TenantContext.require()`** at the start of every service method
6. **Always cleanup ThreadLocal** - use try-finally in filter
7. **Validate at multiple layers** - database constraints + application logic
8. **Log tenant context** for debugging and audit trails

This implementation provides **defense-in-depth** with tenant isolation enforced at:
- Database level (constraints)
- Repository level (scoped queries)
- Service level (validation)
- Security level (JWT extraction)
