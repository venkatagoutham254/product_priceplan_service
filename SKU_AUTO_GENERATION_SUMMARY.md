# SKU Auto-Generation Implementation Summary

## Overview
Implemented automatic SKU code generation system that creates and updates SKU codes based on product name and type. SKU codes are now system-managed and read-only for users.

## SKU Format

### Pattern
- **With Product Type**: `{ProductType}-{Name}-{UniqueCode}`
  - Example: `API-MyProduct-2FXE`
  - Example: `FlatFile-DataProcessor-3G8K`

- **Without Product Type**: `{Name}-{UniqueCode}`
  - Example: `MyProduct-2FXE`
  - Used when product is created without a type initially

### Unique Code Generation
- **Length**: 4 characters
- **Format**: Alphanumeric (A-Z, 0-9)
- **Composition**:
  - First 2 characters: Sequential (base-36) for ordering
  - Last 2 characters: Random for uniqueness
- **Example codes**: `2FXE`, `3G8K`, `1A9Z`

## Implementation Details

### 1. New Service: `SkuGenerationService`
**Location**: `src/main/java/aforo/productrateplanservice/product/service/SkuGenerationService.java`

**Key Methods**:
- `generateSkuCode(Product)` - Generate SKU for new products
- `updateSkuCode(Product)` - Regenerate SKU when product changes
- `shouldRegenerateSku(Product, oldName, oldType)` - Check if regeneration needed

**Features**:
- Sanitizes product names (removes special characters, limits length)
- Combines sequential and random components for unique codes
- Handles null product types gracefully

### 2. Request DTOs Updated
**Files Modified**:
- `CreateProductRequest.java` - Removed `internalSkuCode` field
- `UpdateProductRequest.java` - Removed `internalSkuCode` field

**Impact**: Users can no longer manually set or update SKU codes via API

### 3. Product Service Updates
**File**: `ProductServiceImpl.java`

**Changes**:
- `createProduct()` - Auto-generates SKU on product creation
- `updateProductFully()` - Regenerates SKU if name changes
- `updateProductPartially()` - Regenerates SKU if name changes
- `importExternalProduct()` - Auto-generates SKU for imported products

### 4. Product Type Services Updated
All product type services now regenerate SKU when product type changes:

**Files Modified**:
- `ProductAPIServiceImpl.java`
- `ProductFlatFileServiceImpl.java`
- `ProductLLMTokenServiceImpl.java`
- `ProductSQLResultServiceImpl.java`
- `ProductStorageServiceImpl.java`

**Behavior**: When a product type is assigned or changed, the SKU automatically updates to include the new type prefix.

## User Scenarios

### Scenario 1: Create Product Without Type
```
1. POST /api/products
   Body: { "productName": "MyProduct" }
   
2. Response: { "internalSkuCode": "MyProduct-2FXE" }

3. POST /api/products/{id}/api
   Body: { "endpointUrl": "...", "authType": "..." }
   
4. Response: { "internalSkuCode": "API-MyProduct-2FXE" }
```

### Scenario 2: Create Product With Type
```
1. POST /api/products
   Body: { "productName": "DataService" }
   
2. Response: { "internalSkuCode": "DataService-3G8K" }

3. POST /api/products/{id}/flatfile
   Body: { "fileLocation": "...", "format": "..." }
   
4. Response: { "internalSkuCode": "FlatFile-DataService-3G8K" }
```

### Scenario 3: Update Product Name
```
1. PATCH /api/products/{id}
   Body: { "productName": "NewName" }
   
2. Response: { "internalSkuCode": "API-NewName-4H2L" }
   (New unique code generated)
```

### Scenario 4: Switch Product Type
```
1. Product has type API: "API-MyProduct-2FXE"

2. POST /api/products/{id}/storage
   Body: { "storageLocation": "...", "authType": "..." }
   
3. Response: { "internalSkuCode": "Storage-MyProduct-5J9M" }
   (Type changed, new unique code generated)
```

## API Response Behavior

### All Endpoints Return SKU
- `POST /api/products` - Returns generated SKU
- `PUT /api/products/{id}` - Returns updated SKU (if name changed)
- `PATCH /api/products/{id}` - Returns updated SKU (if name changed)
- `GET /api/products/{id}` - Returns current SKU
- `GET /api/products` - Returns SKU for all products
- `POST /api/products/{id}/{type}` - Returns updated SKU (if type changed)

### SKU is Read-Only
- Users cannot set SKU in request bodies
- SKU is automatically managed by the system
- SKU appears in all response DTOs

## Benefits

1. **Consistency**: All SKUs follow the same format
2. **Uniqueness**: Sequential + random ensures no duplicates
3. **Traceability**: SKU reflects current product name and type
4. **Automation**: No manual SKU management needed
5. **Flexibility**: SKU updates automatically with product changes

## Technical Notes

### Database Constraints
- `internalSkuCode` field remains in Product entity
- Unique constraint on `(organization_id, internal_sku_code)` still enforced
- SKU is generated before saving to database

### Backward Compatibility
- Existing products retain their SKUs
- New products get auto-generated SKUs
- Import functionality uses auto-generation

### Performance
- SKU generation is lightweight (no database queries)
- Sequential counter uses AtomicLong for thread safety
- Random generation uses SecureRandom

## Testing Recommendations

1. **Create Product Without Type**
   - Verify SKU format: `{Name}-{Code}`
   - Verify unique code is 4 characters

2. **Create Product With Type**
   - Add type configuration
   - Verify SKU updates to: `{Type}-{Name}-{Code}`

3. **Update Product Name**
   - Change product name
   - Verify SKU regenerates with new name

4. **Switch Product Type**
   - Change from one type to another
   - Verify SKU updates with new type prefix

5. **Import Product**
   - Import from external source
   - Verify SKU is auto-generated

## Migration Notes

For existing deployments:
- Existing products keep their current SKU codes
- New products will use the auto-generation system
- No data migration required
- API contracts remain compatible (SKU still in responses)
