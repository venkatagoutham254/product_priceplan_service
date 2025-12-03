#!/bin/bash

# Test Product Creation - Source and ExternalId Handling
# This script tests that manual product creation works without requiring source/externalId

BASE_URL="http://localhost:8081"
ORG_ID="1"
JWT_TOKEN="your-jwt-token-here"

echo "=========================================="
echo "Product Creation Tests"
echo "=========================================="
echo ""

# Test 1: Create product WITHOUT source and externalId (should default to MANUAL)
echo "Test 1: Create product without source/externalId"
echo "Expected: Success, source should be 'MANUAL', externalId should be null"
echo ""

RESPONSE=$(curl -s -X POST "${BASE_URL}/api/products" \
  -H "Content-Type: application/json" \
  -H "X-Organization-Id: ${ORG_ID}" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -d '{
    "productName": "Test Manual Product",
    "internalSkuCode": "SKU-MANUAL-001",
    "productDescription": "Product created manually without source",
    "version": "1.0"
  }')

echo "Response:"
echo "$RESPONSE" | jq '.'
echo ""
echo "Checking source field..."
SOURCE=$(echo "$RESPONSE" | jq -r '.source')
if [ "$SOURCE" == "MANUAL" ]; then
    echo "✅ PASS: Source is MANUAL"
else
    echo "❌ FAIL: Source is '$SOURCE', expected 'MANUAL'"
fi
echo ""

# Test 2: Create product WITH explicit source=MANUAL
echo "=========================================="
echo "Test 2: Create product with explicit source=MANUAL"
echo "Expected: Success, source should be 'MANUAL'"
echo ""

RESPONSE=$(curl -s -X POST "${BASE_URL}/api/products" \
  -H "Content-Type: application/json" \
  -H "X-Organization-Id: ${ORG_ID}" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -d '{
    "productName": "Test Manual Product 2",
    "internalSkuCode": "SKU-MANUAL-002",
    "productDescription": "Product with explicit MANUAL source",
    "version": "1.0",
    "source": "MANUAL"
  }')

echo "Response:"
echo "$RESPONSE" | jq '.'
echo ""
SOURCE=$(echo "$RESPONSE" | jq -r '.source')
if [ "$SOURCE" == "MANUAL" ]; then
    echo "✅ PASS: Source is MANUAL"
else
    echo "❌ FAIL: Source is '$SOURCE', expected 'MANUAL'"
fi
echo ""

# Test 3: Create product with lowercase source (should be normalized to uppercase)
echo "=========================================="
echo "Test 3: Create product with lowercase source='manual'"
echo "Expected: Success, source should be normalized to 'MANUAL'"
echo ""

RESPONSE=$(curl -s -X POST "${BASE_URL}/api/products" \
  -H "Content-Type: application/json" \
  -H "X-Organization-Id: ${ORG_ID}" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -d '{
    "productName": "Test Manual Product 3",
    "internalSkuCode": "SKU-MANUAL-003",
    "productDescription": "Product with lowercase source",
    "version": "1.0",
    "source": "manual"
  }')

echo "Response:"
echo "$RESPONSE" | jq '.'
echo ""
SOURCE=$(echo "$RESPONSE" | jq -r '.source')
if [ "$SOURCE" == "MANUAL" ]; then
    echo "✅ PASS: Source normalized to MANUAL"
else
    echo "❌ FAIL: Source is '$SOURCE', expected 'MANUAL'"
fi
echo ""

# Test 4: Import endpoint should still work (requires source + externalId)
echo "=========================================="
echo "Test 4: Import endpoint with KONG source"
echo "Expected: Success, source should be 'KONG', should have import message"
echo ""

RESPONSE=$(curl -s -X POST "${BASE_URL}/api/products/import" \
  -H "Content-Type: application/json" \
  -H "X-Organization-Id: ${ORG_ID}" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -d '{
    "productName": "Kong Test Product",
    "productDescription": "Imported from Kong",
    "source": "KONG",
    "externalId": "kong-test-001",
    "version": "1.0"
  }')

echo "Response:"
echo "$RESPONSE" | jq '.'
echo ""
SOURCE=$(echo "$RESPONSE" | jq -r '.source')
MESSAGE=$(echo "$RESPONSE" | jq -r '.message')
if [ "$SOURCE" == "KONG" ] && [[ "$MESSAGE" == *"KONG"* ]]; then
    echo "✅ PASS: Import endpoint works correctly"
else
    echo "❌ FAIL: Import endpoint issue"
fi
echo ""

# Test 5: Import endpoint with APIGEE
echo "=========================================="
echo "Test 5: Import endpoint with APIGEE source"
echo "Expected: Success, source should be 'APIGEE'"
echo ""

RESPONSE=$(curl -s -X POST "${BASE_URL}/api/products/import" \
  -H "Content-Type: application/json" \
  -H "X-Organization-Id: ${ORG_ID}" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -d '{
    "productName": "Apigee Test Product",
    "productDescription": "Imported from Apigee",
    "source": "APIGEE",
    "externalId": "apigee-test-001",
    "version": "1.0"
  }')

echo "Response:"
echo "$RESPONSE" | jq '.'
echo ""
SOURCE=$(echo "$RESPONSE" | jq -r '.source')
if [ "$SOURCE" == "APIGEE" ]; then
    echo "✅ PASS: Apigee import works correctly"
else
    echo "❌ FAIL: Source is '$SOURCE', expected 'APIGEE'"
fi
echo ""

echo "=========================================="
echo "All tests completed!"
echo "=========================================="
