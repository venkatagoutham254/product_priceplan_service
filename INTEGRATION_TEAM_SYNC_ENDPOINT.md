# 🔄 Sync Endpoint Implementation Guide for Integration Team

## 📋 Overview

This document provides the complete implementation for adding a **SYNC endpoint** to the Apigee Integration Service (Port 8086) that will automatically push products to the ProductRatePlanService (Port 8081).

---

## 🎯 Endpoint to Add

```
POST /api/integrations/apigee/sync
```

**Purpose:** Fetch products from Apigee and automatically push them to ProductRatePlanService

---

## 📝 Complete Implementation

### **1. Create SyncResponse DTO**

```java
package com.aforo.apigee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncResponse {
    private int productsImported;
    private int productsUpdated;
    private int totalSynced;
    private int failed;
    private String message;
}
```

---

### **2. Create ProductImportRequest DTO**

```java
package com.aforo.apigee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImportRequest {
    private String productName;
    private String productDescription;
    private String source;
    private String externalId;
    private String internalSkuCode;
}
```

---

### **3. Create ProductImportResponse DTO**

```java
package com.aforo.apigee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImportResponse {
    private String message;
    private String status;  // "CREATED" or "UPDATED"
    private Long productId;
    private String productName;
    private String source;
    private String externalId;
}
```

---

### **4. Create AforoProductService**

```java
package com.aforo.apigee.service;

import com.aforo.apigee.dto.ProductImportRequest;
import com.aforo.apigee.dto.ProductImportResponse;
import com.aforo.apigee.model.ApigeeProduct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class AforoProductService {
    
    @Value("${aforo.product.service.url:http://localhost:8081}")
    private String productServiceUrl;
    
    private final RestTemplate restTemplate;
    
    public AforoProductService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    /**
     * Push a single product to Aforo ProductRatePlanService
     */
    public ProductImportResponse pushProductToAforo(
        ApigeeProduct apigeeProduct, 
        Long organizationId
    ) {
        log.info("Pushing product {} to Aforo ProductRatePlanService", apigeeProduct.getName());
        
        try {
            // Build request
            ProductImportRequest request = ProductImportRequest.builder()
                .productName(apigeeProduct.getDisplayName())
                .productDescription(apigeeProduct.getDescription() != null 
                    ? apigeeProduct.getDescription() 
                    : "Imported from Apigee")
                .source("APIGEE")
                .externalId(apigeeProduct.getName())
                .internalSkuCode("APIGEE-" + apigeeProduct.getName())
                .build();
            
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Organization-Id", organizationId.toString());
            
            HttpEntity<ProductImportRequest> entity = new HttpEntity<>(request, headers);
            
            // Call Aforo import endpoint
            String url = productServiceUrl + "/api/products/import";
            
            ResponseEntity<ProductImportResponse> response = restTemplate.postForEntity(
                url,
                entity,
                ProductImportResponse.class
            );
            
            ProductImportResponse result = response.getBody();
            
            log.info("✅ Successfully pushed product {} to Aforo. Status: {}, Product ID: {}", 
                     apigeeProduct.getName(), 
                     result.getStatus(), 
                     result.getProductId());
            
            return result;
            
        } catch (Exception e) {
            log.error("❌ Failed to push product {} to Aforo: {}", 
                     apigeeProduct.getName(), 
                     e.getMessage());
            throw new RuntimeException("Failed to push product to Aforo", e);
        }
    }
}
```

---

### **5. Update ApigeeIntegrationController**

```java
package com.aforo.apigee.controller;

import com.aforo.apigee.dto.ProductImportResponse;
import com.aforo.apigee.dto.SyncResponse;
import com.aforo.apigee.model.ApigeeProduct;
import com.aforo.apigee.service.ApigeeService;
import com.aforo.apigee.service.AforoProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/integrations/apigee")
@RequiredArgsConstructor
@Slf4j
public class ApigeeIntegrationController {
    
    private final ApigeeService apigeeService;
    private final AforoProductService aforoProductService;
    
    // Existing endpoint - keep as is
    @GetMapping("/products")
    public ResponseEntity<List<ApigeeProduct>> getProducts() {
        log.info("Fetching products from Apigee");
        List<ApigeeProduct> products = apigeeService.fetchAndSaveProducts();
        return ResponseEntity.ok(products);
    }
    
    // NEW ENDPOINT - Add this
    @PostMapping("/sync")
    public ResponseEntity<SyncResponse> syncProductsToAforo(
        @RequestHeader("X-Organization-Id") Long organizationId
    ) {
        log.info("Starting product sync from Apigee to Aforo for organization: {}", organizationId);
        
        try {
            // 1. Fetch products from Apigee
            List<ApigeeProduct> apigeeProducts = apigeeService.fetchAndSaveProducts();
            log.info("Fetched {} products from Apigee", apigeeProducts.size());
            
            int created = 0;
            int updated = 0;
            int failed = 0;
            
            // 2. Push each product to Aforo
            for (ApigeeProduct product : apigeeProducts) {
                try {
                    ProductImportResponse response = aforoProductService.pushProductToAforo(
                        product, 
                        organizationId
                    );
                    
                    if ("CREATED".equals(response.getStatus())) {
                        created++;
                    } else if ("UPDATED".equals(response.getStatus())) {
                        updated++;
                    }
                    
                } catch (Exception e) {
                    log.error("Failed to sync product {}: {}", product.getName(), e.getMessage());
                    failed++;
                }
            }
            
            // 3. Build response
            SyncResponse syncResponse = SyncResponse.builder()
                .productsImported(created)
                .productsUpdated(updated)
                .totalSynced(created + updated)
                .failed(failed)
                .message(String.format("Sync completed: %d created, %d updated, %d failed", 
                                      created, updated, failed))
                .build();
            
            log.info("Sync completed: {} created, {} updated, {} failed", created, updated, failed);
            
            return ResponseEntity.ok(syncResponse);
            
        } catch (Exception e) {
            log.error("Sync failed: {}", e.getMessage());
            
            SyncResponse errorResponse = SyncResponse.builder()
                .productsImported(0)
                .productsUpdated(0)
                .totalSynced(0)
                .failed(0)
                .message("Sync failed: " + e.getMessage())
                .build();
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    // Keep all other existing endpoints...
}
```

---

### **6. Add RestTemplate Bean (if not exists)**

```java
package com.aforo.apigee.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

---

### **7. Add Configuration in application.yml**

```yaml
aforo:
  product:
    service:
      url: http://localhost:8081  # ProductRatePlanService URL
```

---

## 🧪 Testing the Sync Endpoint

### **Test 1: Trigger Sync**

```bash
curl -X POST http://localhost:8086/api/integrations/apigee/sync \
  -H "X-Organization-Id: 1" \
  -H "Content-Type: application/json"
```

**Expected Response:**
```json
{
  "productsImported": 2,
  "productsUpdated": 0,
  "totalSynced": 2,
  "failed": 0,
  "message": "Sync completed: 2 created, 0 updated, 0 failed"
}
```

---

### **Test 2: Verify Products in Aforo**

```bash
curl -X GET http://localhost:8081/api/products \
  -H "X-Organization-Id: 1"
```

**Expected Response:**
```json
[
  {
    "productId": 3,
    "productName": "pan verify",
    "source": "APIGEE",
    "externalId": "pan",
    "status": "DRAFT"
  },
  {
    "productId": 4,
    "productName": "Product API",
    "source": "APIGEE",
    "externalId": "ProductAPI-Plan",
    "status": "DRAFT"
  }
]
```

---

### **Test 3: Test Idempotency (Run Sync Again)**

```bash
# Run the same sync command again
curl -X POST http://localhost:8086/api/integrations/apigee/sync \
  -H "X-Organization-Id: 1" \
  -H "Content-Type: application/json"
```

**Expected Response:**
```json
{
  "productsImported": 0,
  "productsUpdated": 2,  // ← Changed from created to updated
  "totalSynced": 2,
  "failed": 0,
  "message": "Sync completed: 0 created, 2 updated, 0 failed"
}
```

---

## 📊 Complete Flow Diagram

```
Customer clicks "Sync" button in UI
        ↓
Frontend → POST http://localhost:8086/api/integrations/apigee/sync
        ↓
Integration Service:
  1. Calls apigeeService.fetchAndSaveProducts()
     └─ Fetches from Apigee API
     └─ Returns: [pan, ProductAPI-Plan]
  
  2. For each product:
     └─ Calls aforoProductService.pushProductToAforo()
        └─ POST http://localhost:8081/api/products/import
        └─ Receives: {"status": "CREATED", "productId": 3}
  
  3. Returns sync summary
        ↓
Frontend receives: {"productsImported": 2, "totalSynced": 2}
        ↓
Frontend shows success message
        ↓
Frontend → GET http://localhost:8081/api/products
        ↓
Displays all products (manual + imported)
```

---

## 🔍 Logging Output

When sync runs, you should see:

```
INFO - Starting product sync from Apigee to Aforo for organization: 1
INFO - Fetched 2 products from Apigee
INFO - Pushing product pan to Aforo ProductRatePlanService
INFO - ✅ Successfully pushed product pan to Aforo. Status: CREATED, Product ID: 3
INFO - Pushing product ProductAPI-Plan to Aforo ProductRatePlanService
INFO - ✅ Successfully pushed product ProductAPI-Plan to Aforo. Status: CREATED, Product ID: 4
INFO - Sync completed: 2 created, 0 updated, 0 failed
```

---

## ⚠️ Error Handling

The implementation includes:
- ✅ Try-catch for each product (one failure doesn't stop the sync)
- ✅ Detailed logging with ✅/❌ emojis
- ✅ Failed count in response
- ✅ Graceful error messages

---

## 🎯 Summary

**What This Adds:**
1. ✅ `POST /api/integrations/apigee/sync` endpoint
2. ✅ Automatic fetching from Apigee
3. ✅ Automatic pushing to ProductRatePlanService
4. ✅ Idempotency (handles duplicates)
5. ✅ Error handling and logging
6. ✅ Sync statistics in response

**Dependencies:**
- ProductRatePlanService must be running on port 8081
- Endpoint `/api/products/import` must be accessible
- `X-Organization-Id` header is required

**Result:**
- Customer clicks ONE button
- Products automatically sync from Apigee to Aforo
- Products appear in UI immediately

---

## 📞 Contact

If you have questions about this implementation, contact the ProductRatePlanService team.

**ProductRatePlanService Endpoints:**
- Import: `POST http://localhost:8081/api/products/import`
- List: `GET http://localhost:8081/api/products`

Both endpoints are ready and tested! ✅
