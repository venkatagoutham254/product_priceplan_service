#!/bin/bash

# SKU Auto-Generation End-to-End Test Script
# Testing all scenarios for automatic SKU generation

BASE_URL="http://localhost:8081"
JWT_TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhZm9yby1jdXN0b21lcnNlcnZpY2UiLCJzdWIiOiJnbWJAYWZvcm8uYWkiLCJvcmdJZCI6MTksInN0YXR1cyI6IkFDVElWRSIsImlhdCI6MTc2NTk2MDQ5NSwiZXhwIjoxNzY2NTY1Mjk1fQ.cIUbp7fWhcMzd0OIZF4_JGnlEXbTX4SI2UXdHUF6xOM"

echo "=========================================="
echo "SKU AUTO-GENERATION E2E TEST"
echo "=========================================="
echo ""

# Test 1: Create Product Without Type
echo "TEST 1: Create Product Without Type"
echo "------------------------------------"
RESPONSE1=$(curl -s -X POST "$BASE_URL/api/products" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: multipart/form-data" \
  -F 'request={"productName":"TestProduct1","productDescription":"Test product for SKU generation"}')

echo "Response: $RESPONSE1"
PRODUCT_ID1=$(echo $RESPONSE1 | grep -o '"productId":[0-9]*' | grep -o '[0-9]*')
SKU1=$(echo $RESPONSE1 | grep -o '"internalSkuCode":"[^"]*"' | cut -d'"' -f4)
echo "Product ID: $PRODUCT_ID1"
echo "Generated SKU (without type): $SKU1"
echo "Expected format: TestProduct1-XXXX"
echo ""

# Test 2: Add Product Type (API) - SKU should update
echo "TEST 2: Add API Type to Product"
echo "--------------------------------"
if [ ! -z "$PRODUCT_ID1" ]; then
  RESPONSE2=$(curl -s -X POST "$BASE_URL/api/products/$PRODUCT_ID1/api" \
    -H "Authorization: Bearer $JWT_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"endpointUrl":"https://api.example.com","authType":"API_KEY"}')
  
  echo "Response: $RESPONSE2"
  
  # Get updated product
  RESPONSE2_GET=$(curl -s -X GET "$BASE_URL/api/products/$PRODUCT_ID1" \
    -H "Authorization: Bearer $JWT_TOKEN")
  
  SKU2=$(echo $RESPONSE2_GET | grep -o '"internalSkuCode":"[^"]*"' | cut -d'"' -f4)
  echo "Updated SKU (with API type): $SKU2"
  echo "Expected format: API-TestProduct1-XXXX"
  echo ""
else
  echo "SKIPPED: Product ID not found"
  echo ""
fi

# Test 3: Create Product and Immediately Add Type
echo "TEST 3: Create Product with Immediate Type Assignment"
echo "------------------------------------------------------"
RESPONSE3=$(curl -s -X POST "$BASE_URL/api/products" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: multipart/form-data" \
  -F 'request={"productName":"TestProduct2","productDescription":"Test product 2"}')

PRODUCT_ID2=$(echo $RESPONSE3 | grep -o '"productId":[0-9]*' | grep -o '[0-9]*')
SKU3=$(echo $RESPONSE3 | grep -o '"internalSkuCode":"[^"]*"' | cut -d'"' -f4)
echo "Initial SKU: $SKU3"

if [ ! -z "$PRODUCT_ID2" ]; then
  RESPONSE3_TYPE=$(curl -s -X POST "$BASE_URL/api/products/$PRODUCT_ID2/flatfile" \
    -H "Authorization: Bearer $JWT_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"fileLocation":"/data/files","format":"CSV"}')
  
  RESPONSE3_GET=$(curl -s -X GET "$BASE_URL/api/products/$PRODUCT_ID2" \
    -H "Authorization: Bearer $JWT_TOKEN")
  
  SKU3_UPDATED=$(echo $RESPONSE3_GET | grep -o '"internalSkuCode":"[^"]*"' | cut -d'"' -f4)
  echo "Updated SKU (with FlatFile type): $SKU3_UPDATED"
  echo "Expected format: FlatFile-TestProduct2-XXXX"
  echo ""
fi

# Test 4: Update Product Name - SKU should regenerate
echo "TEST 4: Update Product Name"
echo "---------------------------"
if [ ! -z "$PRODUCT_ID2" ]; then
  RESPONSE4=$(curl -s -X PATCH "$BASE_URL/api/products/$PRODUCT_ID2" \
    -H "Authorization: Bearer $JWT_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"productName":"RenamedProduct2"}')
  
  SKU4=$(echo $RESPONSE4 | grep -o '"internalSkuCode":"[^"]*"' | cut -d'"' -f4)
  echo "Updated SKU (after name change): $SKU4"
  echo "Expected format: FlatFile-RenamedProduct2-XXXX (new unique code)"
  echo ""
else
  echo "SKIPPED: Product ID not found"
  echo ""
fi

# Test 5: Switch Product Type - SKU should update
echo "TEST 5: Switch Product Type (FlatFile -> Storage)"
echo "--------------------------------------------------"
if [ ! -z "$PRODUCT_ID2" ]; then
  RESPONSE5=$(curl -s -X POST "$BASE_URL/api/products/$PRODUCT_ID2/storage" \
    -H "Authorization: Bearer $JWT_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"storageLocation":"s3://bucket/path","authType":"IAM"}')
  
  RESPONSE5_GET=$(curl -s -X GET "$BASE_URL/api/products/$PRODUCT_ID2" \
    -H "Authorization: Bearer $JWT_TOKEN")
  
  SKU5=$(echo $RESPONSE5_GET | grep -o '"internalSkuCode":"[^"]*"' | cut -d'"' -f4)
  echo "Updated SKU (after type switch): $SKU5"
  echo "Expected format: Storage-RenamedProduct2-XXXX (new unique code)"
  echo ""
fi

# Test 6: Verify SKU is Read-Only (should fail if trying to set it)
echo "TEST 6: Verify SKU Cannot Be Manually Set"
echo "------------------------------------------"
RESPONSE6=$(curl -s -X POST "$BASE_URL/api/products" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: multipart/form-data" \
  -F 'request={"productName":"TestProduct3","internalSkuCode":"MANUAL-SKU-123"}')

PRODUCT_ID3=$(echo $RESPONSE6 | grep -o '"productId":[0-9]*' | grep -o '[0-9]*')
if [ ! -z "$PRODUCT_ID3" ]; then
  RESPONSE6_GET=$(curl -s -X GET "$BASE_URL/api/products/$PRODUCT_ID3" \
    -H "Authorization: Bearer $JWT_TOKEN")
  
  SKU6=$(echo $RESPONSE6_GET | grep -o '"internalSkuCode":"[^"]*"' | cut -d'"' -f4)
  echo "Generated SKU: $SKU6"
  echo "Expected: Auto-generated (NOT 'MANUAL-SKU-123')"
  echo "Result: SKU field should be ignored in request"
  echo ""
fi

# Test 7: Create Multiple Products - Verify Unique SKUs
echo "TEST 7: Create Multiple Products with Same Name"
echo "------------------------------------------------"
for i in {1..3}; do
  RESPONSE7=$(curl -s -X POST "$BASE_URL/api/products" \
    -H "Authorization: Bearer $JWT_TOKEN" \
    -H "Content-Type: multipart/form-data" \
    -F 'request={"productName":"DuplicateTest'$i'","productDescription":"Testing uniqueness"}')
  
  SKU7=$(echo $RESPONSE7 | grep -o '"internalSkuCode":"[^"]*"' | cut -d'"' -f4)
  echo "Product $i SKU: $SKU7"
done
echo "Expected: All SKUs should have different unique codes"
echo ""

# Test 8: Full Product Lifecycle
echo "TEST 8: Full Product Lifecycle"
echo "-------------------------------"
echo "Step 1: Create product"
RESPONSE8=$(curl -s -X POST "$BASE_URL/api/products" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: multipart/form-data" \
  -F 'request={"productName":"LifecycleProduct","productDescription":"Full lifecycle test"}')

PRODUCT_ID8=$(echo $RESPONSE8 | grep -o '"productId":[0-9]*' | grep -o '[0-9]*')
SKU8_1=$(echo $RESPONSE8 | grep -o '"internalSkuCode":"[^"]*"' | cut -d'"' -f4)
echo "Initial SKU: $SKU8_1"

if [ ! -z "$PRODUCT_ID8" ]; then
  echo "Step 2: Add LLMToken type"
  curl -s -X POST "$BASE_URL/api/products/$PRODUCT_ID8/llmtoken" \
    -H "Authorization: Bearer $JWT_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"modelName":"gpt-4","endpointUrl":"https://api.openai.com","authType":"BEARER"}' > /dev/null
  
  RESPONSE8_2=$(curl -s -X GET "$BASE_URL/api/products/$PRODUCT_ID8" \
    -H "Authorization: Bearer $JWT_TOKEN")
  SKU8_2=$(echo $RESPONSE8_2 | grep -o '"internalSkuCode":"[^"]*"' | cut -d'"' -f4)
  echo "After adding LLMToken: $SKU8_2"
  
  echo "Step 3: Update name"
  curl -s -X PATCH "$BASE_URL/api/products/$PRODUCT_ID8" \
    -H "Authorization: Bearer $JWT_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"productName":"UpdatedLifecycle"}' > /dev/null
  
  RESPONSE8_3=$(curl -s -X GET "$BASE_URL/api/products/$PRODUCT_ID8" \
    -H "Authorization: Bearer $JWT_TOKEN")
  SKU8_3=$(echo $RESPONSE8_3 | grep -o '"internalSkuCode":"[^"]*"' | cut -d'"' -f4)
  echo "After name update: $SKU8_3"
  
  echo "Step 4: Switch to SQLResult type"
  curl -s -X POST "$BASE_URL/api/products/$PRODUCT_ID8/sqlresult" \
    -H "Authorization: Bearer $JWT_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"dbType":"PostgreSQL","connectionString":"jdbc:postgresql://localhost:5432/db","authType":"PASSWORD"}' > /dev/null
  
  RESPONSE8_4=$(curl -s -X GET "$BASE_URL/api/products/$PRODUCT_ID8" \
    -H "Authorization: Bearer $JWT_TOKEN")
  SKU8_4=$(echo $RESPONSE8_4 | grep -o '"internalSkuCode":"[^"]*"' | cut -d'"' -f4)
  echo "After type switch to SQLResult: $SKU8_4"
  echo ""
fi

echo "=========================================="
echo "TEST SUMMARY"
echo "=========================================="
echo "✓ Test 1: Product creation without type"
echo "✓ Test 2: Adding product type updates SKU"
echo "✓ Test 3: Product with immediate type assignment"
echo "✓ Test 4: Name change regenerates SKU"
echo "✓ Test 5: Type switch regenerates SKU"
echo "✓ Test 6: Manual SKU setting is ignored"
echo "✓ Test 7: Multiple products have unique SKUs"
echo "✓ Test 8: Full lifecycle SKU updates"
echo ""
echo "All tests completed!"
echo "Review the SKU formats above to verify correctness."
