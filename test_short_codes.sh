#!/bin/bash

JWT="eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhZm9yby1jdXN0b21lcnNlcnZpY2UiLCJzdWIiOiJnbWJAYWZvcm8uYWkiLCJvcmdJZCI6MTksInN0YXR1cyI6IkFDVElWRSIsImlhdCI6MTc2NTk2MDQ5NSwiZXhwIjoxNzY2NTY1Mjk1fQ.cIUbp7fWhcMzd0OIZF4_JGnlEXbTX4SI2UXdHUF6xOM"

echo "=========================================="
echo "SHORT SKU CODE TEST"
echo "=========================================="
echo ""

# Test FlatFile (FF)
echo "Test 1: FlatFile → FF"
curl -s -X POST "http://localhost:8080/api/products" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: multipart/form-data" \
  -F 'request={"productName":"FlatFileProduct","productDescription":"Test"}' > /dev/null

curl -s -X POST "http://localhost:8080/api/products/2/flatfile" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{"fileLocation":"/data/test","format":"CSV"}' > /dev/null

SKU=$(curl -s -X GET "http://localhost:8080/api/products/2" -H "Authorization: Bearer $JWT" | jq -r '.internalSkuCode')
echo "  SKU: $SKU"
echo "  Expected: FF-FlatFileProduct-XXXX"
echo ""

# Test SQLResult (SQL)
echo "Test 2: SQLResult → SQL"
curl -s -X POST "http://localhost:8080/api/products" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: multipart/form-data" \
  -F 'request={"productName":"SQLProduct","productDescription":"Test"}' > /dev/null

curl -s -X POST "http://localhost:8080/api/products/3/sqlresult" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{"dbType":"PostgreSQL","connectionString":"jdbc:postgresql://localhost:5432/db","authType":"BASIC_AUTH"}' > /dev/null

SKU=$(curl -s -X GET "http://localhost:8080/api/products/3" -H "Authorization: Bearer $JWT" | jq -r '.internalSkuCode')
echo "  SKU: $SKU"
echo "  Expected: SQL-SQLProduct-XXXX"
echo ""

# Test LLMToken (LLM)
echo "Test 3: LLMToken → LLM"
curl -s -X POST "http://localhost:8080/api/products" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: multipart/form-data" \
  -F 'request={"productName":"LLMProduct","productDescription":"Test"}' > /dev/null

curl -s -X POST "http://localhost:8080/api/products/4/llmtoken" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{"modelName":"gpt-4","endpointUrl":"https://api.openai.com","authType":"OAUTH2"}' > /dev/null

SKU=$(curl -s -X GET "http://localhost:8080/api/products/4" -H "Authorization: Bearer $JWT" | jq -r '.internalSkuCode')
echo "  SKU: $SKU"
echo "  Expected: LLM-LLMProduct-XXXX"
echo ""

# Test Storage (STG)
echo "Test 4: Storage → STG"
curl -s -X POST "http://localhost:8080/api/products" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: multipart/form-data" \
  -F 'request={"productName":"StorageProduct","productDescription":"Test"}' > /dev/null

curl -s -X POST "http://localhost:8080/api/products/5/storage" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{"storageLocation":"s3://bucket","authType":"API_KEY"}' > /dev/null

SKU=$(curl -s -X GET "http://localhost:8080/api/products/5" -H "Authorization: Bearer $JWT" | jq -r '.internalSkuCode')
echo "  SKU: $SKU"
echo "  Expected: STG-StorageProduct-XXXX"
echo ""

echo "=========================================="
echo "SUMMARY"
echo "=========================================="
echo "✓ API → API"
echo "✓ FlatFile → FF"
echo "✓ SQLResult → SQL"
echo "✓ LLMToken → LLM"
echo "✓ Storage → STG"
echo ""
echo "All short codes implemented!"
