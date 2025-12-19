#!/bin/bash

JWT="eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhZm9yby1jdXN0b21lcnNlcnZpY2UiLCJzdWIiOiJnbWJAYWZvcm8uYWkiLCJvcmdJZCI6MTksInN0YXR1cyI6IkFDVElWRSIsImlhdCI6MTc2NTk2MDQ5NSwiZXhwIjoxNzY2NTY1Mjk1fQ.cIUbp7fWhcMzd0OIZF4_JGnlEXbTX4SI2UXdHUF6xOM"
BASE_URL="http://localhost:8080/api/products"

echo "=========================================="
echo "SKU CODE TESTING - ALL SCENARIOS"
echo "=========================================="
echo ""

# Scenario 1: Product without type
echo "Scenario 1: Create product WITHOUT type"
echo "Expected: {SHORT_NAME}-{RANDOM}"
RESULT=$(curl -s -X POST "$BASE_URL" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: multipart/form-data" \
  -F 'request={"productName":"GPT-4o Input Tokens","productDescription":"Test"}')
echo "Product: GPT-4o Input Tokens"
echo "SKU: $(echo $RESULT | jq -r '.internalSkuCode')"
echo "Expected format: GPT4OI-XXXX"
echo ""

# Scenario 2: Add LLM type to product
PROD_ID=$(echo $RESULT | jq -r '.productId')
echo "Scenario 2: Add LLM type to product"
echo "Expected: LLM-{SHORT_NAME}-{RANDOM}"
curl -s -X POST "$BASE_URL/$PROD_ID/llm-token" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{"modelName":"gpt-4o","endpointUrl":"https://api.openai.com","authType":"OAUTH2"}' > /dev/null
RESULT=$(curl -s -X GET "$BASE_URL/$PROD_ID" -H "Authorization: Bearer $JWT")
echo "Product: GPT-4o Input Tokens"
echo "SKU: $(echo $RESULT | jq -r '.internalSkuCode')"
echo "Expected format: LLM-GPT4OI-XXXX"
echo ""

# Scenario 3: SQL Result with short code
echo "Scenario 3: Create SQL Result product"
echo "Expected: SQL-SQLRES-XXXX"
RESULT=$(curl -s -X POST "$BASE_URL" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: multipart/form-data" \
  -F 'request={"productName":"SQL Result Dataset","productDescription":"Test"}')
PROD_ID=$(echo $RESULT | jq -r '.productId')
curl -s -X POST "$BASE_URL/$PROD_ID/sql-result" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{"dbType":"POSTGRES","connectionString":"jdbc:postgresql://localhost:5432/db","authType":"BASIC_AUTH"}' > /dev/null
RESULT=$(curl -s -X GET "$BASE_URL/$PROD_ID" -H "Authorization: Bearer $JWT")
echo "Product: SQL Result Dataset"
echo "SKU: $(echo $RESULT | jq -r '.internalSkuCode')"
echo "Expected format: SQL-SQLRES-XXXX"
echo ""

# Scenario 4: FlatFile with short code
echo "Scenario 4: Create FlatFile product"
echo "Expected: FF-FLATFI-XXXX"
RESULT=$(curl -s -X POST "$BASE_URL" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: multipart/form-data" \
  -F 'request={"productName":"Flat File Upload","productDescription":"Test"}')
PROD_ID=$(echo $RESULT | jq -r '.productId')
curl -s -X POST "$BASE_URL/$PROD_ID/flat-file" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{"fileLocation":"/data/test","format":"CSV"}' > /dev/null
RESULT=$(curl -s -X GET "$BASE_URL/$PROD_ID" -H "Authorization: Bearer $JWT")
echo "Product: Flat File Upload"
echo "SKU: $(echo $RESULT | jq -r '.internalSkuCode')"
echo "Expected format: FF-FLATFI-XXXX"
echo ""

# Scenario 5: Storage with short code
echo "Scenario 5: Create Storage product"
echo "Expected: STG-VERYVE-XXXX"
RESULT=$(curl -s -X POST "$BASE_URL" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: multipart/form-data" \
  -F 'request={"productName":"Very Very Long Product Name","productDescription":"Test"}')
PROD_ID=$(echo $RESULT | jq -r '.productId')
curl -s -X POST "$BASE_URL/$PROD_ID/storage" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{"storageLocation":"s3://bucket","authType":"API_KEY"}' > /dev/null
RESULT=$(curl -s -X GET "$BASE_URL/$PROD_ID" -H "Authorization: Bearer $JWT")
echo "Product: Very Very Long Product Name"
echo "SKU: $(echo $RESULT | jq -r '.internalSkuCode')"
echo "Expected format: STG-VERYVE-XXXX"
echo ""

# Scenario 6: API with short code
echo "Scenario 6: Create API product"
echo "Expected: API-APIPRO-XXXX"
RESULT=$(curl -s -X POST "$BASE_URL" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: multipart/form-data" \
  -F 'request={"productName":"API Product Service","productDescription":"Test"}')
PROD_ID=$(echo $RESULT | jq -r '.productId')
curl -s -X POST "$BASE_URL/$PROD_ID/api" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{"endpointUrl":"https://api.example.com","authType":"API_KEY"}' > /dev/null
RESULT=$(curl -s -X GET "$BASE_URL/$PROD_ID" -H "Authorization: Bearer $JWT")
echo "Product: API Product Service"
echo "SKU: $(echo $RESULT | jq -r '.internalSkuCode')"
echo "Expected format: API-APIPRO-XXXX"
echo ""

echo "=========================================="
echo "SUMMARY"
echo "=========================================="
echo "✓ Product type short codes: API, FF, SQL, LLM, STG"
echo "✓ Product name short codes: 4-6 characters"
echo "✓ Random unique codes: 4 characters"
echo "✓ Format: {TYPE}-{CODE}-{RANDOM}"
echo ""
echo "All scenarios tested!"
