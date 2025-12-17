# Tenant Isolation Verification Checklist

## ✅ Implementation Verification

Run these checks to verify tenant isolation is working correctly:

### 1. Database Schema Verification

```sql
-- Check Product table structure
DESCRIBE aforo_product;
-- Verify organization_id column exists and is NOT NULL

-- Check RatePlan table structure  
DESCRIBE aforo_rate_plan;
-- Verify organization_id column exists and is NOT NULL

-- Check existing data
SELECT product_id, product_name, organization_id FROM aforo_product;
SELECT rate_plan_id, rate_plan_name, organization_id FROM aforo_rate_plan;

-- Verify no NULL organization_id values
SELECT COUNT(*) FROM aforo_product WHERE organization_id IS NULL;
SELECT COUNT(*) FROM aforo_rate_plan WHERE organization_id IS NULL;
-- Both should return 0
```

### 2. JWT Token Verification

**Decode your JWT token** at https://jwt.io and verify it contains one of these claims:
- `organizationId`
- `orgId`
- `tenantId`
- `organization_id`
- `org_id`
- `tenant`

Example valid JWT payload:
```json
{
  "sub": "user@example.com",
  "organizationId": 123,
  "iat": 1234567890,
  "exp": 1234567890
}
```

### 3. API Testing with Different Organizations

#### Test 1: Create Product for Org 1
```bash
# JWT with organizationId: 1
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer <JWT_ORG_1>" \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Product Org 1",
    "internalSkuCode": "PROD-ORG1-001"
  }'

# Expected: Success, returns product with ID (e.g., 100)
```

#### Test 2: Create Product for Org 2
```bash
# JWT with organizationId: 2
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer <JWT_ORG_2>" \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Product Org 2",
    "internalSkuCode": "PROD-ORG2-001"
  }'

# Expected: Success, returns product with ID (e.g., 101)
```

#### Test 3: List Products for Org 1
```bash
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer <JWT_ORG_1>"

# Expected: Returns only "Product Org 1" (ID: 100)
# Should NOT include "Product Org 2"
```

#### Test 4: List Products for Org 2
```bash
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer <JWT_ORG_2>"

# Expected: Returns only "Product Org 2" (ID: 101)
# Should NOT include "Product Org 1"
```

#### Test 5: Cross-Tenant Access Attempt (Should Fail)
```bash
# Try to access Org 1's product using Org 2's JWT
curl -X GET http://localhost:8080/api/products/100 \
  -H "Authorization: Bearer <JWT_ORG_2>"

# Expected: 404 Not Found
# Message: "Product not found with id: 100"
```

#### Test 6: Update Cross-Tenant Product (Should Fail)
```bash
# Try to update Org 1's product using Org 2's JWT
curl -X PATCH http://localhost:8080/api/products/100 \
  -H "Authorization: Bearer <JWT_ORG_2>" \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Hacked Product"
  }'

# Expected: 404 Not Found
```

#### Test 7: Delete Cross-Tenant Product (Should Fail)
```bash
# Try to delete Org 1's product using Org 2's JWT
curl -X DELETE http://localhost:8080/api/products/100 \
  -H "Authorization: Bearer <JWT_ORG_2>"

# Expected: 404 Not Found
```

### 4. RatePlan Cross-Tenant Testing

#### Test 8: Create RatePlan for Org 1's Product
```bash
# Create rate plan for product 100 (Org 1) using Org 1 JWT
curl -X POST http://localhost:8080/api/rateplans \
  -H "Authorization: Bearer <JWT_ORG_1>" \
  -H "Content-Type: application/json" \
  -d '{
    "ratePlanName": "Premium Plan",
    "productId": 100,
    "billingFrequency": "MONTHLY"
  }'

# Expected: Success, returns rate plan with ID (e.g., 200)
```

#### Test 9: Create RatePlan with Cross-Tenant Product (Should Fail)
```bash
# Try to create rate plan for product 100 (Org 1) using Org 2 JWT
curl -X POST http://localhost:8080/api/rateplans \
  -H "Authorization: Bearer <JWT_ORG_2>" \
  -H "Content-Type: application/json" \
  -d '{
    "ratePlanName": "Hacker Plan",
    "productId": 100,
    "billingFrequency": "MONTHLY"
  }'

# Expected: 404 Not Found
# Message: "Product not found with ID: 100"
```

#### Test 10: List RatePlans by Product (Cross-Tenant)
```bash
# Try to list rate plans for product 100 (Org 1) using Org 2 JWT
curl -X GET http://localhost:8080/api/rateplans/product/100 \
  -H "Authorization: Bearer <JWT_ORG_2>"

# Expected: Empty list []
# Should NOT return Org 1's rate plans
```

### 5. Testing with X-Organization-Id Header (Development Only)

For easier testing during development, you can use the `X-Organization-Id` header:

```bash
# Create product for Org 1
curl -X POST http://localhost:8080/api/products \
  -H "X-Organization-Id: 1" \
  -H "Authorization: Bearer <ANY_VALID_JWT>" \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Test Product",
    "internalSkuCode": "TEST-001"
  }'

# List products for Org 1
curl -X GET http://localhost:8080/api/products \
  -H "X-Organization-Id: 1" \
  -H "Authorization: Bearer <ANY_VALID_JWT>"

# List products for Org 2
curl -X GET http://localhost:8080/api/products \
  -H "X-Organization-Id: 2" \
  -H "Authorization: Bearer <ANY_VALID_JWT>"
```

**⚠️ WARNING**: Remove or disable the `X-Organization-Id` header support in production!

### 6. Application Logs Verification

Check application logs for tenant context messages:

```bash
# Start the application and watch logs
tail -f logs/application.log

# Look for these log messages:
[DEBUG] JwtTenantFilter - Tenant set from JWT claim 'organizationId': 123
[DEBUG] JwtTenantFilter - Tenant set from X-Organization-Id header: 123
[DEBUG] JwtTenantFilter - No tenant claim found in JWT
```

### 7. Database Verification After Tests

```sql
-- Verify products are correctly assigned to organizations
SELECT product_id, product_name, organization_id FROM aforo_product;

-- Expected results:
-- 100 | Product Org 1 | 1
-- 101 | Product Org 2 | 2

-- Verify rate plans are correctly assigned
SELECT rp.rate_plan_id, rp.rate_plan_name, rp.organization_id, rp.product_id, p.organization_id as product_org_id
FROM aforo_rate_plan rp
LEFT JOIN aforo_product p ON rp.product_id = p.product_id;

-- Verify organization_id matches between rate plan and product
-- Both organization_id columns should always match
```

### 8. Security Audit Queries

```sql
-- Find any orphaned rate plans (product's org != rate plan's org)
SELECT rp.rate_plan_id, rp.organization_id as rp_org, p.organization_id as prod_org
FROM aforo_rate_plan rp
JOIN aforo_product p ON rp.product_id = p.product_id
WHERE rp.organization_id != p.organization_id;
-- Should return 0 rows

-- Find any NULL organization_id values
SELECT 'Product' as entity, COUNT(*) as null_count FROM aforo_product WHERE organization_id IS NULL
UNION ALL
SELECT 'RatePlan', COUNT(*) FROM aforo_rate_plan WHERE organization_id IS NULL;
-- Both should be 0
```

---

## Expected Results Summary

| Test | Expected Result |
|------|----------------|
| Create product with Org 1 JWT | ✅ Success |
| Create product with Org 2 JWT | ✅ Success |
| List products with Org 1 JWT | ✅ Returns only Org 1 products |
| List products with Org 2 JWT | ✅ Returns only Org 2 products |
| Get Org 1 product with Org 2 JWT | ❌ 404 Not Found |
| Update Org 1 product with Org 2 JWT | ❌ 404 Not Found |
| Delete Org 1 product with Org 2 JWT | ❌ 404 Not Found |
| Create rate plan for Org 1 product with Org 1 JWT | ✅ Success |
| Create rate plan for Org 1 product with Org 2 JWT | ❌ 404 Not Found |
| List rate plans for Org 1 product with Org 2 JWT | ✅ Empty list |

---

## Troubleshooting

### Issue: All tests return 401 Unauthorized
**Cause**: JWT token is invalid or expired  
**Solution**: Generate a new valid JWT token with proper claims

### Issue: Tests pass but products are visible across organizations
**Possible Causes**:
1. JWT doesn't contain `organizationId` claim
2. Database has old data with NULL `organization_id`
3. Using same `organizationId` in both test JWTs

**Solutions**:
1. Verify JWT payload contains `organizationId` claim
2. Run: `UPDATE aforo_product SET organization_id = 1 WHERE organization_id IS NULL;`
3. Use different organization IDs (1 and 2) in test JWTs

### Issue: X-Organization-Id header doesn't work
**Cause**: Header is case-sensitive or contains invalid value  
**Solution**: Use exact header name: `X-Organization-Id: 123` (numeric value)

### Issue: 500 Internal Server Error
**Cause**: Missing tenant context or database constraint violation  
**Solution**: Check application logs for stack trace and error details

---

## Production Readiness Checklist

Before deploying to production:

- [ ] Verify all database tables have `organization_id` column with NOT NULL constraint
- [ ] Verify unique constraints include `organization_id`
- [ ] Verify JWT tokens contain `organizationId` claim
- [ ] Run all 10 API tests above and verify expected results
- [ ] Run security audit queries and verify 0 violations
- [ ] Disable or remove `X-Organization-Id` header support in `JwtTenantFilter`
- [ ] Add database indexes on `organization_id` columns
- [ ] Set up monitoring/alerting for cross-tenant access attempts
- [ ] Document JWT claim requirements for organization-service
- [ ] Verify `spring.jpa.hibernate.ddl-auto` is NOT `create` or `create-drop`
- [ ] Verify Liquibase is properly configured or disabled

---

## Quick Test Script

Save this as `test-tenant-isolation.sh`:

```bash
#!/bin/bash

# Configuration
BASE_URL="http://localhost:8080"
JWT_ORG_1="<YOUR_JWT_FOR_ORG_1>"
JWT_ORG_2="<YOUR_JWT_FOR_ORG_2>"

echo "=== Testing Tenant Isolation ==="

# Test 1: Create product for Org 1
echo -e "\n1. Creating product for Org 1..."
PRODUCT_1=$(curl -s -X POST "$BASE_URL/api/products" \
  -H "Authorization: Bearer $JWT_ORG_1" \
  -H "Content-Type: application/json" \
  -d '{"productName":"Product Org 1","internalSkuCode":"PROD-ORG1-001"}')
echo "Response: $PRODUCT_1"
PRODUCT_1_ID=$(echo $PRODUCT_1 | jq -r '.productId')
echo "Product 1 ID: $PRODUCT_1_ID"

# Test 2: Create product for Org 2
echo -e "\n2. Creating product for Org 2..."
PRODUCT_2=$(curl -s -X POST "$BASE_URL/api/products" \
  -H "Authorization: Bearer $JWT_ORG_2" \
  -H "Content-Type: application/json" \
  -d '{"productName":"Product Org 2","internalSkuCode":"PROD-ORG2-001"}')
echo "Response: $PRODUCT_2"
PRODUCT_2_ID=$(echo $PRODUCT_2 | jq -r '.productId')
echo "Product 2 ID: $PRODUCT_2_ID"

# Test 3: List products for Org 1
echo -e "\n3. Listing products for Org 1..."
curl -s -X GET "$BASE_URL/api/products" \
  -H "Authorization: Bearer $JWT_ORG_1" | jq '.'

# Test 4: List products for Org 2
echo -e "\n4. Listing products for Org 2..."
curl -s -X GET "$BASE_URL/api/products" \
  -H "Authorization: Bearer $JWT_ORG_2" | jq '.'

# Test 5: Cross-tenant access (should fail)
echo -e "\n5. Attempting cross-tenant access (Org 2 accessing Org 1's product)..."
curl -s -X GET "$BASE_URL/api/products/$PRODUCT_1_ID" \
  -H "Authorization: Bearer $JWT_ORG_2" | jq '.'

echo -e "\n=== Tests Complete ==="
```

Run with: `chmod +x test-tenant-isolation.sh && ./test-tenant-isolation.sh`
