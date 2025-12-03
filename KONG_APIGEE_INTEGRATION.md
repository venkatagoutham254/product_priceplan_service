# Kong & Apigee Product Import Integration Guide

## Overview
This service supports importing products from Kong and Apigee API gateway integrations. Products imported from these sources are automatically configured as **API** product types.

## Implementation Status ✅

### Database Schema
- ✅ `source` column (VARCHAR 50) - Tracks product origin (MANUAL, KONG, APIGEE)
- ✅ `external_id` column (VARCHAR 255) - Stores external system ID
- ✅ Composite index on (external_id, source, organization_id) for performance
- ✅ Default value 'MANUAL' for source column

### Entity & Repository
- ✅ Product entity includes `source` and `externalId` fields
- ✅ Repository method: `findByExternalIdAndSourceAndOrganizationId()`
- ✅ ProductType enum includes API type

### Service Layer
- ✅ Import endpoint: `POST /api/products/import`
- ✅ Auto-sets ProductType to API
- ✅ Source validation (KONG/APIGEE only)
- ✅ Auto-generates SKU if not provided
- ✅ Handles create/update logic based on external_id + source

## API Endpoint

### Import Product
```
POST /api/products/import
Content-Type: application/json
X-Organization-Id: {organizationId}
Authorization: Bearer {jwt_token}
```

### Request Body
```json
{
  "productName": "Kong API Product",
  "productDescription": "Product imported from Kong",
  "source": "KONG",
  "externalId": "kong-product-123",
  "version": "1.0",
  "internalSkuCode": "KONG-123"  // Optional - auto-generated if not provided
}
```

### Response
```json
{
  "message": "Product imported successfully from KONG",
  "status": "CREATED",  // or "UPDATED"
  "productId": 1,
  "productName": "Kong API Product",
  "source": "KONG",
  "externalId": "kong-product-123"
}
```

## Integration Examples

### From Kong Service

```bash
curl -X POST http://localhost:8081/api/products/import \
  -H "Content-Type: application/json" \
  -H "X-Organization-Id: 1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "productName": "Payment API",
    "productDescription": "Kong Payment Gateway API",
    "source": "KONG",
    "externalId": "kong-payment-api-001"
  }'
```

**What happens:**
1. ✅ Validates source is "KONG" or "APIGEE"
2. ✅ Checks if product exists by `externalId` + `source` + `organizationId`
3. ✅ If exists: Updates product name and description
4. ✅ If new: Creates product with:
   - `source` = "KONG"
   - `productType` = ProductType.API (auto-set)
   - `internalSkuCode` = "KONG-kong-payment-api-001" (auto-generated)
   - `status` = DRAFT

### From Apigee Service

```bash
curl -X POST http://localhost:8081/api/products/import \
  -H "Content-Type: application/json" \
  -H "X-Organization-Id: 1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "productName": "Analytics API",
    "productDescription": "Apigee Analytics API Product",
    "source": "APIGEE",
    "externalId": "analytics-api-product",
    "version": "2.0"
  }'
```

**What happens:**
1. ✅ Validates source is "KONG" or "APIGEE"
2. ✅ Auto-sets `productType` = ProductType.API
3. ✅ Auto-generates SKU: "APIGEE-analytics-api-product"
4. ✅ Creates/updates product in DRAFT status

## Key Features

### 1. Automatic ProductType Assignment
- **All imported products** automatically get `ProductType.API`
- No manual selection needed
- Consistent with API gateway integration use case

### 2. Source Validation
- Only accepts `"KONG"` or `"APIGEE"` as valid sources
- Case-insensitive (automatically converted to uppercase)
- Rejects invalid sources with clear error message

### 3. SKU Auto-Generation
- If `internalSkuCode` not provided: `{SOURCE}-{externalId}`
- Examples:
  - Kong: `KONG-payment-api-001`
  - Apigee: `APIGEE-analytics-product`
- Can override by providing custom SKU in request

### 4. Duplicate Prevention
- Uses composite key: `(externalId, source, organizationId)`
- Same externalId can exist for different sources
- Example: `KONG-api-001` and `APIGEE-api-001` are different products

### 5. Update vs Create Logic
- **Existing product**: Updates name, description, version
- **New product**: Creates with all fields + auto-sets ProductType.API
- Returns status: `"CREATED"` or `"UPDATED"`

## Validation Rules

### Required Fields
| Field | Required | Notes |
|-------|----------|-------|
| productName | ✅ Yes | From external system |
| source | ✅ Yes | Must be "KONG" or "APIGEE" |
| externalId | ✅ Yes | Unique ID from source system |
| internalSkuCode | ❌ No | Auto-generated if not provided |
| productDescription | ❌ No | Optional |
| version | ❌ No | Optional |

### Field Constraints
- `productName`: Max 255 characters
- `internalSkuCode`: Max 100 characters
- `productDescription`: Max 1000 characters
- `source`: Must be "KONG" or "APIGEE" (case-insensitive)

## Error Handling

### Invalid Source
```json
{
  "error": "Invalid source. Must be 'KONG' or 'APIGEE'"
}
```

### Missing Required Fields
```json
{
  "error": "External ID is required for product import"
}
```

### Missing Product Name
```json
{
  "error": "Product name is required for product import"
}
```

## Integration Flow Diagram

```
┌─────────────┐
│ Kong/Apigee │
│   Service   │
└──────┬──────┘
       │
       │ POST /api/products/import
       │ {productName, source, externalId}
       ▼
┌──────────────────────────────────┐
│  Product/RatePlan Service        │
│                                  │
│  1. Validate source (KONG/APIGEE)│
│  2. Check if exists by           │
│     (externalId + source + org)  │
│  3. If exists → Update           │
│     If new → Create              │
│  4. Auto-set ProductType = API   │
│  5. Auto-generate SKU if needed  │
│  6. Save product                 │
└──────┬───────────────────────────┘
       │
       │ Response: {productId, status, ...}
       ▼
┌─────────────┐
│ Kong/Apigee │
│   Service   │
└─────────────┘
```

## Testing

### Test 1: Import from Kong (New Product)
```bash
curl -X POST http://localhost:8081/api/products/import \
  -H "Content-Type: application/json" \
  -H "X-Organization-Id: 1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "productName": "Test Kong Product",
    "productDescription": "Testing Kong import",
    "source": "kong",
    "externalId": "test-kong-123"
  }'
```

**Expected Response:**
```json
{
  "message": "Product imported successfully from KONG",
  "status": "CREATED",
  "productId": 1,
  "productName": "Test Kong Product",
  "source": "KONG",
  "externalId": "test-kong-123"
}
```

**Verify in Database:**
```sql
SELECT product_id, product_name, source, external_id, product_type, internal_sku_code, status
FROM aforo_product
WHERE external_id = 'test-kong-123' AND source = 'KONG';
```

**Expected:**
- `product_type` = 'API'
- `internal_sku_code` = 'KONG-test-kong-123'
- `status` = 'DRAFT'

### Test 2: Import from Apigee (New Product)
```bash
curl -X POST http://localhost:8081/api/products/import \
  -H "Content-Type: application/json" \
  -H "X-Organization-Id: 1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "productName": "Test Apigee Product",
    "productDescription": "Testing Apigee import",
    "source": "apigee",
    "externalId": "test-apigee-456",
    "internalSkuCode": "CUSTOM-SKU-001"
  }'
```

**Expected Response:**
```json
{
  "message": "Product imported successfully from APIGEE",
  "status": "CREATED",
  "productId": 2,
  "productName": "Test Apigee Product",
  "source": "APIGEE",
  "externalId": "test-apigee-456"
}
```

**Verify:**
- `product_type` = 'API'
- `internal_sku_code` = 'CUSTOM-SKU-001' (custom SKU used)
- `status` = 'DRAFT'

### Test 3: Update Existing Product
```bash
# Import same product again with updated name
curl -X POST http://localhost:8081/api/products/import \
  -H "Content-Type: application/json" \
  -H "X-Organization-Id: 1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "productName": "Updated Kong Product",
    "productDescription": "Updated description",
    "source": "kong",
    "externalId": "test-kong-123"
  }'
```

**Expected Response:**
```json
{
  "message": "Product imported successfully from KONG",
  "status": "UPDATED",
  "productId": 1,
  "productName": "Updated Kong Product",
  "source": "KONG",
  "externalId": "test-kong-123"
}
```

### Test 4: Invalid Source
```bash
curl -X POST http://localhost:8081/api/products/import \
  -H "Content-Type: application/json" \
  -H "X-Organization-Id: 1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "productName": "Invalid Product",
    "source": "INVALID",
    "externalId": "test-123"
  }'
```

**Expected:** HTTP 400 with error message

## Multi-Tenant Support

The import endpoint is **multi-tenant aware**:
- Requires `X-Organization-Id` header
- Products are scoped to organization
- Same `externalId` can exist across different organizations
- Composite uniqueness: `(externalId, source, organizationId)`

## Next Steps After Import

1. **Configure API Details**: Use product type-specific endpoints to add API configuration
   ```
   POST /api/products/{productId}/api
   ```

2. **Create Rate Plans**: Attach pricing models to the imported product
   ```
   POST /api/rate-plans
   ```

3. **Finalize Product**: Move from DRAFT to ACTIVE status
   ```
   POST /api/products/{productId}/finalize
   ```

## Migration Notes

### For Existing Products
If you have existing products that need to be marked as imported:

```sql
-- Mark existing products as Kong imports
UPDATE aforo_product 
SET source = 'KONG', 
    external_id = 'kong-{original-id}',
    product_type = 'API'
WHERE product_id IN (1, 2, 3);

-- Mark existing products as Apigee imports
UPDATE aforo_product 
SET source = 'APIGEE', 
    external_id = 'apigee-{original-id}',
    product_type = 'API'
WHERE product_id IN (4, 5, 6);
```

## Troubleshooting

### Issue: "External ID is required for product import"
**Solution:** Ensure `externalId` field is provided in request body

### Issue: "Invalid source. Must be 'KONG' or 'APIGEE'"
**Solution:** Check `source` field - must be exactly "KONG" or "APIGEE" (case-insensitive)

### Issue: Duplicate SKU error
**Solution:** Either:
- Provide custom `internalSkuCode` in request
- Ensure `externalId` is unique within the organization

### Issue: Product not updating
**Solution:** Verify the combination of `externalId`, `source`, and `organizationId` matches existing product

## API Reference Summary

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/api/products/import` | POST | Import product from Kong/Apigee |
| `/api/products` | GET | List all products (includes imported) |
| `/api/products/{id}` | GET | Get product details |
| `/api/products/{id}` | PATCH | Update product |
| `/api/products/{id}/finalize` | POST | Finalize product (DRAFT → ACTIVE) |

## Support

For issues or questions:
1. Check logs for detailed error messages
2. Verify database schema matches expected structure
3. Ensure JWT token has correct organization scope
4. Validate request payload against schema
