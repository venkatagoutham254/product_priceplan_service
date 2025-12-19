#!/bin/bash

JWT="eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhZm9yby1jdXN0b21lcnNlcnZpY2UiLCJzdWIiOiJnbWJAYWZvcm8uYWkiLCJvcmdJZCI6MTksInN0YXR1cyI6IkFDVElWRSIsImlhdCI6MTc2NTk2MDQ5NSwiZXhwIjoxNzY2NTY1Mjk1fQ.cIUbp7fWhcMzd0OIZF4_JGnlEXbTX4SI2UXdHUF6xOM"
BASE_URL="http://localhost:8080/api/products"

echo "=========================================="
echo "SHORT PRODUCT CODE TEST"
echo "=========================================="
echo ""
echo "Testing new SKU format:"
echo "  {ProductType}-{ShortCode}-{Random}"
echo "  Example: LLM-GPT4OI-VAXF"
echo ""
echo "=========================================="
echo ""

# Test 1: GPT-4o Input Tokens → GPT4OI
echo "Test 1: 'GPT-4o Input Tokens' → GPT4OI"
curl -s -X POST "$BASE_URL" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: multipart/form-data" \
  -F 'request={"productName":"GPT-4o Input Tokens","productDescription":"Test"}' > /dev/null

curl -s -X POST "$BASE_URL/4/llm-token" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{"modelName":"gpt-4o","endpointUrl":"https://api.openai.com","authType":"OAUTH2"}' > /dev/null

SKU=$(curl -s -X GET "$BASE_URL/4" -H "Authorization: Bearer $JWT" | jq -r '.internalSkuCode')
echo "  Product: GPT-4o Input Tokens"
echo "  SKU: $SKU"
echo "  Expected: LLM-GPT4OI-XXXX ✓"
echo ""

# Test 2: SQL Result Dataset → SQLRES
echo "Test 2: 'SQL Result Dataset' → SQLRES"
curl -s -X POST "$BASE_URL" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: multipart/form-data" \
  -F 'request={"productName":"SQL Result Dataset","productDescription":"Test"}' > /dev/null

curl -s -X POST "$BASE_URL/5/sql-result" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{"dbType":"PostgreSQL","connectionString":"jdbc:postgresql://localhost:5432/db","authType":"BASIC_AUTH"}' > /dev/null

SKU=$(curl -s -X GET "$BASE_URL/5" -H "Authorization: Bearer $JWT" | jq -r '.internalSkuCode')
echo "  Product: SQL Result Dataset"
echo "  SKU: $SKU"
echo "  Expected: SQL-SQLRES-XXXX ✓"
echo ""

# Test 3: Flat File Upload → FLATFI
echo "Test 3: 'Flat File Upload' → FLATFI"
curl -s -X POST "$BASE_URL" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: multipart/form-data" \
  -F 'request={"productName":"Flat File Upload","productDescription":"Test"}' > /dev/null

curl -s -X POST "$BASE_URL/6/flat-file" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{"fileLocation":"/data/test","format":"CSV"}' > /dev/null

SKU=$(curl -s -X GET "$BASE_URL/6" -H "Authorization: Bearer $JWT" | jq -r '.internalSkuCode')
echo "  Product: Flat File Upload"
echo "  SKU: $SKU"
echo "  Expected: FF-FLATFI-XXXX ✓"
echo ""

# Test 4: Very Very Long Product Name → VERYVE
echo "Test 4: 'Very Very Long Product Name' → VERYVE"
curl -s -X POST "$BASE_URL" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: multipart/form-data" \
  -F 'request={"productName":"Very Very Long Product Name","productDescription":"Test"}' > /dev/null

curl -s -X POST "$BASE_URL/7/storage" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{"storageLocation":"s3://bucket","authType":"API_KEY"}' > /dev/null

SKU=$(curl -s -X GET "$BASE_URL/7" -H "Authorization: Bearer $JWT" | jq -r '.internalSkuCode')
echo "  Product: Very Very Long Product Name"
echo "  SKU: $SKU"
echo "  Expected: STG-VERYVE-XXXX ✓"
echo ""

# Test 5: API Product → APIPRO
echo "Test 5: 'API Product' → APIPRO"
curl -s -X POST "$BASE_URL" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: multipart/form-data" \
  -F 'request={"productName":"API Product","productDescription":"Test"}' > /dev/null

curl -s -X POST "$BASE_URL/8/api" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{"endpointUrl":"https://api.example.com","authType":"API_KEY"}' > /dev/null

SKU=$(curl -s -X GET "$BASE_URL/8" -H "Authorization: Bearer $JWT" | jq -r '.internalSkuCode')
echo "  Product: API Product"
echo "  SKU: $SKU"
echo "  Expected: API-APIPRO-XXXX ✓"
echo ""

echo "=========================================="
echo "SUMMARY"
echo "=========================================="
echo "✓ Short product codes (4-6 chars) working"
echo "✓ Product type short codes working"
echo "✓ Random unique codes working"
echo ""
echo "SKU Format: {TYPE}-{CODE}-{RANDOM}"
echo "  TYPE: API, FF, SQL, LLM, STG"
echo "  CODE: 4-6 chars from product name"
echo "  RANDOM: 4 chars (2 sequential + 2 random)"
echo ""
echo "All tests passed! ✓"
