# Quick Start: Kong & Apigee Product Import

## ✅ Implementation Complete

Your Product/RatePlan service now supports importing products from Kong and Apigee!

## What Was Implemented

### 1. Database Schema ✅
- `source` column (MANUAL, KONG, APIGEE)
- `external_id` column (stores external system ID)
- Composite index for performance
- Already exists in your database!

### 2. Code Updates ✅
- **CreateProductRequest**: Removed strict validation for imports
- **ProductServiceImpl.importExternalProduct()**: 
  - ✅ Auto-sets `ProductType.API`
  - ✅ Validates source (KONG/APIGEE only)
  - ✅ Auto-generates SKU: `{SOURCE}-{externalId}`
  - ✅ Handles create/update logic
  - ✅ Multi-tenant support

### 3. API Endpoint ✅
```
POST /api/products/import
```

## Quick Test

### 1. Import from Kong
```bash
curl -X POST http://localhost:8081/api/products/import \
  -H "Content-Type: application/json" \
  -H "X-Organization-Id: 1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "productName": "Kong Payment API",
    "productDescription": "Payment API from Kong",
    "source": "kong",
    "externalId": "kong-payment-001"
  }'
```

**Expected Result:**
```json
{
  "message": "Product imported successfully from KONG",
  "status": "CREATED",
  "productId": 1,
  "productName": "Kong Payment API",
  "source": "KONG",
  "externalId": "kong-payment-001"
}
```

**What Happens Automatically:**
- ✅ `productType` = API
- ✅ `internalSkuCode` = "KONG-kong-payment-001"
- ✅ `status` = DRAFT

### 2. Import from Apigee
```bash
curl -X POST http://localhost:8081/api/products/import \
  -H "Content-Type: application/json" \
  -H "X-Organization-Id: 1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "productName": "Apigee Analytics API",
    "source": "apigee",
    "externalId": "analytics-api"
  }'
```

**What Happens Automatically:**
- ✅ `productType` = API
- ✅ `internalSkuCode` = "APIGEE-analytics-api"
- ✅ `status` = DRAFT

## Key Features

| Feature | Description |
|---------|-------------|
| **Auto ProductType** | Always sets to `API` - no manual selection needed |
| **Source Validation** | Only accepts "KONG" or "APIGEE" |
| **Auto SKU** | Generates `{SOURCE}-{externalId}` if not provided |
| **Duplicate Prevention** | Uses `(externalId, source, organizationId)` |
| **Update Support** | Re-importing updates existing product |

## Testing Tools Provided

### 1. Bash Test Script
```bash
./test-import.sh
```
- Tests all scenarios
- Includes error cases
- Easy to customize

### 2. Postman Collection
Import `Kong_Apigee_Import.postman_collection.json` into Postman:
- 10 pre-configured requests
- Success and error test cases
- Environment variables for easy configuration

### 3. Full Documentation
See `KONG_APIGEE_INTEGRATION.md` for:
- Complete API reference
- Integration flow diagrams
- Troubleshooting guide
- Database queries

## Validation Rules

### Required Fields
- ✅ `productName` - Product name from external system
- ✅ `source` - Must be "KONG" or "APIGEE"
- ✅ `externalId` - Unique ID from source system

### Optional Fields
- `internalSkuCode` - Auto-generated if not provided
- `productDescription` - Optional description
- `version` - Optional version string

## Error Messages

| Error | Cause | Solution |
|-------|-------|----------|
| "Invalid source. Must be 'KONG' or 'APIGEE'" | Invalid source value | Use "KONG" or "APIGEE" |
| "External ID is required for product import" | Missing externalId | Add externalId to request |
| "Product name is required for product import" | Missing productName | Add productName to request |

## Next Steps

After importing a product:

1. **Add API Configuration** (if needed)
   ```
   POST /api/products/{productId}/api
   ```

2. **Create Rate Plans**
   ```
   POST /api/rate-plans
   ```

3. **Finalize Product**
   ```
   POST /api/products/{productId}/finalize
   ```

## Files Modified

1. ✅ `CreateProductRequest.java` - Removed strict validation
2. ✅ `ProductServiceImpl.java` - Enhanced import logic
3. ✅ Database schema - Already has required columns

## Files Created

1. 📄 `KONG_APIGEE_INTEGRATION.md` - Full documentation
2. 📄 `QUICK_START_IMPORT.md` - This file
3. 📄 `test-import.sh` - Bash test script
4. 📄 `Kong_Apigee_Import.postman_collection.json` - Postman tests

## Verify Implementation

### Check Database
```sql
SELECT product_id, product_name, source, external_id, product_type, internal_sku_code, status
FROM aforo_product
WHERE source IN ('KONG', 'APIGEE');
```

### Check Logs
Look for:
```
Importing product [...] from source [KONG] with externalId [...]
Created new product from source [KONG] with externalId [...], ProductType set to API
Imported product [...] from source [KONG] with ProductType: API
```

## Common Use Cases

### Use Case 1: Kong Service Calls Import
```java
// In Kong service
ProductImportRequest request = ProductImportRequest.builder()
    .productName(kongProduct.getName())
    .productDescription(kongProduct.getDescription())
    .source("KONG")
    .externalId(kongProduct.getId())
    .build();

// Call Product/RatePlan service
restTemplate.postForObject(
    "http://product-service:8081/api/products/import",
    request,
    ProductImportResponse.class
);
```

### Use Case 2: Apigee Service Calls Import
```java
// In Apigee service
ProductImportRequest request = ProductImportRequest.builder()
    .productName(apigeeProduct.getDisplayName())
    .productDescription("Imported from Apigee")
    .source("APIGEE")
    .externalId(apigeeProduct.getName())
    .build();

// Call Product/RatePlan service
restTemplate.postForObject(
    "http://product-service:8081/api/products/import",
    request,
    ProductImportResponse.class
);
```

## Support

Need help?
1. Check `KONG_APIGEE_INTEGRATION.md` for detailed docs
2. Review application logs for error details
3. Verify JWT token and organization ID
4. Test with Postman collection

---

**Ready to use!** 🚀

The implementation is complete and ready for Kong and Apigee services to start importing products.
