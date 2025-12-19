package aforo.productrateplanservice.product.service;

import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.enums.ProductType;
import aforo.productrateplanservice.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service responsible for generating and updating SKU codes for products.
 * SKU Format: {productType}-{name}-{uniqueCode}
 * Example: API-MyProduct-2FXE
 */
@Service
@RequiredArgsConstructor
public class SkuGenerationService {

    private final ProductRepository productRepository;
    private static final AtomicLong sequenceCounter = new AtomicLong(System.currentTimeMillis() % 10000);
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();
    private static final int UNIQUE_CODE_LENGTH = 4;

    /**
     * Generate SKU code for a product based on its current state
     * @param product The product entity
     * @return Generated SKU code
     */
    public String generateSkuCode(Product product) {
        String productName = product.getProductName();
        ProductType productType = product.getProductType();
        
        // Generate unique code
        String uniqueCode = generateUniqueCode();
        
        // Build SKU based on whether product type exists
        if (productType != null) {
            // Format: {shortTypeCode}-{name}-{uniqueCode}
            return String.format("%s-%s-%s", 
                getShortTypeCode(productType), 
                sanitizeName(productName), 
                uniqueCode);
        } else {
            // Format: {name}-{uniqueCode} (when no product type yet)
            return String.format("%s-%s", 
                sanitizeName(productName), 
                uniqueCode);
        }
    }
    
    /**
     * Convert ProductType to short code
     * API → API
     * FlatFile → FF
     * SQLResult → SQL
     * LLMToken → LLM
     * Storage → STG
     * @param productType The product type
     * @return Short code for the product type
     */
    private String getShortTypeCode(ProductType productType) {
        if (productType == null) {
            return "";
        }
        
        switch (productType) {
            case API:
                return "API";
            case FlatFile:
                return "FF";
            case SQLResult:
                return "SQL";
            case LLMToken:
                return "LLM";
            case Storage:
                return "STG";
            default:
                return productType.name().toUpperCase();
        }
    }

    /**
     * Update SKU code when product name or type changes
     * @param product The product entity
     * @return Updated SKU code
     */
    public String updateSkuCode(Product product) {
        return generateSkuCode(product);
    }

    /**
     * Generate a unique alphanumeric code with sequential order
     * Combines sequential counter with random characters for uniqueness
     * @return 4-character unique code (e.g., "2FXE")
     */
    private String generateUniqueCode() {
        // Get next sequence number
        long sequence = sequenceCounter.getAndIncrement();
        
        // Convert sequence to base-36 and combine with random characters
        StringBuilder code = new StringBuilder();
        
        // First 2 characters: sequential (base-36)
        String seqPart = Long.toString(sequence, 36).toUpperCase();
        if (seqPart.length() >= 2) {
            code.append(seqPart.substring(seqPart.length() - 2));
        } else {
            code.append("0").append(seqPart);
        }
        
        // Last 2 characters: random alphanumeric
        for (int i = 0; i < 2; i++) {
            code.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
        }
        
        return code.toString();
    }

    /**
     * Generate short product code from product name for SKU usage
     * - Convert to uppercase
     * - Remove spaces & special characters
     * - Keep ONLY first 4-6 characters
     * - If name is missing → use "GEN"
     * 
     * Examples:
     * - "GPT-4o Input Tokens" → "GPT4O"
     * - "SQL Result Dataset" → "SQLR"
     * - "Flat File Upload" → "FLAT"
     * - "Very Very Long Product Name" → "VERY"
     * - (empty/null) → "GEN"
     * 
     * @param name Product name
     * @return Short product code (4-6 characters)
     */
    private String sanitizeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "GEN";
        }
        
        // Convert to uppercase and remove spaces & special characters
        String sanitized = name.toUpperCase()
                              .replaceAll("[^A-Z0-9]", "");
        
        // If nothing left after sanitization, use default
        if (sanitized.isEmpty()) {
            return "GEN";
        }
        
        // Keep only first 4-6 characters (prefer 6 if available, minimum 4)
        int maxLength = Math.min(6, sanitized.length());
        int minLength = Math.min(4, sanitized.length());
        
        // Use 6 chars if available, otherwise use what we have (min 4, max 6)
        return sanitized.substring(0, maxLength);
    }

    /**
     * Check if SKU code needs to be regenerated
     * This happens when name or product type changes
     * @param product The product entity
     * @param oldName Previous product name
     * @param oldType Previous product type
     * @return true if SKU needs regeneration
     */
    public boolean shouldRegenerateSku(Product product, String oldName, ProductType oldType) {
        String currentName = product.getProductName();
        ProductType currentType = product.getProductType();
        
        // Name changed
        boolean nameChanged = !sanitizeName(currentName).equals(sanitizeName(oldName));
        
        // Type changed (including null to non-null or vice versa)
        boolean typeChanged = (oldType == null && currentType != null) ||
                              (oldType != null && !oldType.equals(currentType));
        
        return nameChanged || typeChanged;
    }
}
