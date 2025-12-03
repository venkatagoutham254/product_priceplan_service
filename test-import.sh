#!/bin/bash

# Test script for Kong/Apigee Product Import
# Usage: ./test-import.sh

# Configuration
BASE_URL="http://localhost:8081"
ORG_ID="1"
JWT_TOKEN="YOUR_JWT_TOKEN_HERE"  # Replace with actual JWT token

echo "========================================="
echo "Kong/Apigee Product Import Test Script"
echo "========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test 1: Import from Kong (New Product)
echo -e "${YELLOW}Test 1: Import new product from Kong${NC}"
echo "----------------------------------------"
RESPONSE=$(curl -s -X POST "${BASE_URL}/api/products/import" \
  -H "Content-Type: application/json" \
  -H "X-Organization-Id: ${ORG_ID}" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -d '{
    "productName": "Kong Payment API",
    "productDescription": "Payment processing API from Kong",
    "source": "kong",
    "externalId": "kong-payment-001",
    "version": "1.0"
  }')

echo "Response: $RESPONSE"
echo ""

# Test 2: Import from Apigee (New Product)
echo -e "${YELLOW}Test 2: Import new product from Apigee${NC}"
echo "----------------------------------------"
RESPONSE=$(curl -s -X POST "${BASE_URL}/api/products/import" \
  -H "Content-Type: application/json" \
  -H "X-Organization-Id: ${ORG_ID}" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -d '{
    "productName": "Apigee Analytics API",
    "productDescription": "Analytics API from Apigee",
    "source": "apigee",
    "externalId": "apigee-analytics-001",
    "version": "2.0"
  }')

echo "Response: $RESPONSE"
echo ""

# Test 3: Update existing Kong product
echo -e "${YELLOW}Test 3: Update existing Kong product${NC}"
echo "----------------------------------------"
RESPONSE=$(curl -s -X POST "${BASE_URL}/api/products/import" \
  -H "Content-Type: application/json" \
  -H "X-Organization-Id: ${ORG_ID}" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -d '{
    "productName": "Kong Payment API v2",
    "productDescription": "Updated payment processing API",
    "source": "kong",
    "externalId": "kong-payment-001",
    "version": "2.0"
  }')

echo "Response: $RESPONSE"
echo ""

# Test 4: Import with custom SKU
echo -e "${YELLOW}Test 4: Import with custom SKU${NC}"
echo "----------------------------------------"
RESPONSE=$(curl -s -X POST "${BASE_URL}/api/products/import" \
  -H "Content-Type: application/json" \
  -H "X-Organization-Id: ${ORG_ID}" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -d '{
    "productName": "Kong User API",
    "productDescription": "User management API",
    "source": "kong",
    "externalId": "kong-user-001",
    "internalSkuCode": "CUSTOM-USER-API-001"
  }')

echo "Response: $RESPONSE"
echo ""

# Test 5: Invalid source (should fail)
echo -e "${YELLOW}Test 5: Invalid source (expected to fail)${NC}"
echo "----------------------------------------"
RESPONSE=$(curl -s -X POST "${BASE_URL}/api/products/import" \
  -H "Content-Type: application/json" \
  -H "X-Organization-Id: ${ORG_ID}" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -d '{
    "productName": "Invalid Product",
    "source": "INVALID_SOURCE",
    "externalId": "test-001"
  }')

echo "Response: $RESPONSE"
echo ""

# Test 6: Missing external ID (should fail)
echo -e "${YELLOW}Test 6: Missing external ID (expected to fail)${NC}"
echo "----------------------------------------"
RESPONSE=$(curl -s -X POST "${BASE_URL}/api/products/import" \
  -H "Content-Type: application/json" \
  -H "X-Organization-Id: ${ORG_ID}" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -d '{
    "productName": "Missing External ID",
    "source": "kong"
  }')

echo "Response: $RESPONSE"
echo ""

# Test 7: Get all products to verify imports
echo -e "${YELLOW}Test 7: Get all products${NC}"
echo "----------------------------------------"
RESPONSE=$(curl -s -X GET "${BASE_URL}/api/products" \
  -H "X-Organization-Id: ${ORG_ID}" \
  -H "Authorization: Bearer ${JWT_TOKEN}")

echo "Response: $RESPONSE"
echo ""

echo "========================================="
echo -e "${GREEN}Test script completed!${NC}"
echo "========================================="
echo ""
echo "Next steps:"
echo "1. Review the responses above"
echo "2. Check database for imported products"
echo "3. Verify ProductType is set to 'API'"
echo "4. Verify SKU auto-generation"
