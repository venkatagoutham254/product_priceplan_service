# Product Price Plan Service - API Documentation

## Overview

The Product Price Plan Service is a comprehensive Spring Boot microservice that manages products and their associated rate plans with various pricing models. This service provides RESTful APIs for creating, managing, and calculating pricing for different product types and billing strategies.

**Base URL:** `http://localhost:8080`  
**API Version:** v1  
**Content-Type:** `application/json`

## Table of Contents

1. [Authentication](#authentication)
2. [Core APIs](#core-apis)
   - [Health Check](#health-check)
   - [Home/Discovery](#homediscovery)
3. [Product Management](#product-management)
4. [Product Type Configurations](#product-type-configurations)
5. [Rate Plan Management](#rate-plan-management)
6. [Pricing Models](#pricing-models)
7. [Additional Features](#additional-features)
8. [Revenue Estimation](#revenue-estimation)
9. [Error Handling](#error-handling)
10. [Data Models](#data-models)

---

## Authentication

The service uses JWT-based authentication. Include the JWT token in the Authorization header:

```
Authorization: Bearer <jwt-token>
```

---

## Core APIs

### Health Check

#### Get Service Health Status
```http
GET /api/health
```

**Description:** Returns the health status of the service for monitoring and load balancing.

**Response:**
```json
{
  "status": "UP"
}
```

---

### Home/Discovery

#### Get API Discovery Links
```http
GET /
```

**Description:** Returns HATEOAS links to discover available API endpoints.

**Response:**
```json
{
  "_links": {
    "products": {
      "href": "http://localhost:8080/api/products"
    },
    "ratePlans": {
      "href": "http://localhost:8080/api/rateplans"
    },
    "setupFees": {
      "href": "http://localhost:8080/api/rateplans/1/setupfees"
    },
    "discounts": {
      "href": "http://localhost:8080/api/rateplans/1/discounts"
    },
    "freemiums": {
      "href": "http://localhost:8080/api/rateplans/1/freemiums"
    },
    "minimumCommitments": {
      "href": "http://localhost:8080/api/rateplans/1/minimumcommitments"
    }
  }
}
```

---

## Product Management

### Create Product (Multipart)
```http
POST /api/products
Content-Type: multipart/form-data
```

**Description:** Create a new product with optional icon file upload.

**Request Parameters:**
- `request` (required): JSON string containing product details
- `icon` (optional): Image file for product icon

**Request JSON Structure:**
```json
{
  "productName": "API Gateway Service",
  "version": "1.0.0",
  "internalSkuCode": "AGS-001",
  "productDescription": "Enterprise API Gateway with advanced features"
}
```

**Response:**
```json
{
  "productId": 1,
  "productName": "API Gateway Service",
  "version": "1.0.0",
  "productDescription": "Enterprise API Gateway with advanced features",
  "status": "DRAFT",
  "productType": null,
  "internalSkuCode": "AGS-001",
  "icon": "http://localhost:8080/uploads/products/1/icon.png",
  "createdOn": "2024-11-04T10:30:00",
  "lastUpdated": "2024-11-04T10:30:00",
  "billableMetrics": []
}
```

### Get Product by ID
```http
GET /api/products/{id}
```

**Description:** Retrieve a specific product by its ID.

**Path Parameters:**
- `id` (Long): Product ID

**Response:** Same as Create Product response

### List All Products
```http
GET /api/products
```

**Description:** Retrieve all products in the system.

**Response:**
```json
[
  {
    "productId": 1,
    "productName": "API Gateway Service",
    "version": "1.0.0",
    "productDescription": "Enterprise API Gateway with advanced features",
    "status": "DRAFT",
    "productType": "API",
    "internalSkuCode": "AGS-001",
    "icon": "http://localhost:8080/uploads/products/1/icon.png",
    "createdOn": "2024-11-04T10:30:00",
    "lastUpdated": "2024-11-04T10:30:00",
    "billableMetrics": []
  }
]
```

### Update Product (Full)
```http
PUT /api/products/{id}
```

**Description:** Fully update a product (all fields required).

**Path Parameters:**
- `id` (Long): Product ID

**Request Body:**
```json
{
  "productName": "Updated API Gateway Service",
  "version": "1.1.0",
  "internalSkuCode": "AGS-001-V2",
  "productDescription": "Updated enterprise API Gateway with new features"
}
```

### Update Product (Partial)
```http
PATCH /api/products/{id}
```

**Description:** Partially update a product (only provided fields are updated).

**Path Parameters:**
- `id` (Long): Product ID

**Request Body:**
```json
{
  "productDescription": "Updated description only"
}
```

### Update Product Icon
```http
PATCH /api/products/{id}/icon
Content-Type: multipart/form-data
```

**Description:** Update only the product icon.

**Path Parameters:**
- `id` (Long): Product ID

**Request Parameters:**
- `icon` (required): New image file for product icon

### Get Product Icon
```http
GET /api/products/{id}/icon
```

**Description:** Get product icon (returns redirect to icon URL).

**Path Parameters:**
- `id` (Long): Product ID

**Response:** HTTP 302 redirect to icon URL

### Delete Product
```http
DELETE /api/products/{id}
```

**Description:** Delete a product and all associated data.

**Path Parameters:**
- `id` (Long): Product ID

**Response:** HTTP 204 No Content

### Delete Product Icon
```http
DELETE /api/products/{id}/icon
```

**Description:** Delete only the product icon.

**Path Parameters:**
- `id` (Long): Product ID

**Response:** HTTP 204 No Content

### Finalize Product
```http
POST /api/products/{id}/finalize
```

**Description:** Finalize a product, changing its status to ACTIVE.

**Path Parameters:**
- `id` (Long): Product ID

**Response:** Updated product with ACTIVE status

### Clear Product Type Configuration
```http
DELETE /api/products/{id}/configuration
```

**Description:** Remove all product type configurations to allow switching to a different product type.

**Path Parameters:**
- `id` (Long): Product ID

**Response:** HTTP 204 No Content

---

## Product Type Configurations

### API Product Type

#### Create API Configuration
```http
POST /api/products/{productId}/api
```

**Request Body:**
```json
{
  "requestsPerSecond": 1000,
  "maxConcurrentConnections": 500,
  "supportedMethods": ["GET", "POST", "PUT", "DELETE"],
  "authenticationRequired": true,
  "rateLimitingEnabled": true
}
```

#### Get API Configuration
```http
GET /api/products/{productId}/api
```

#### Update API Configuration (Full)
```http
PUT /api/products/{productId}/api
```

#### Update API Configuration (Partial)
```http
PATCH /api/products/{productId}/api
```

#### Delete API Configuration
```http
DELETE /api/products/{productId}/api
```

#### List All API Configurations
```http
GET /api/products/api
```

### Flat File Product Type

#### Create Flat File Configuration
```http
POST /api/products/{productId}/flatfile
```

**Request Body:**
```json
{
  "maxFileSize": "100MB",
  "supportedFormats": ["CSV", "JSON", "XML"],
  "compressionEnabled": true,
  "encryptionRequired": false
}
```

#### Get Flat File Configuration
```http
GET /api/products/{productId}/flatfile
```

#### Update Flat File Configuration (Full)
```http
PUT /api/products/{productId}/flatfile
```

#### Update Flat File Configuration (Partial)
```http
PATCH /api/products/{productId}/flatfile
```

#### Delete Flat File Configuration
```http
DELETE /api/products/{productId}/flatfile
```

#### List All Flat File Configurations
```http
GET /api/products/flatfile
```

### LLM Token Product Type

#### Create LLM Token Configuration
```http
POST /api/products/{productId}/llm-token
```

**Request Body:**
```json
{
  "modelName": "GPT-4",
  "maxTokensPerRequest": 4096,
  "supportedLanguages": ["en", "es", "fr"],
  "contextWindowSize": 8192
}
```

#### Get LLM Token Configuration
```http
GET /api/products/{productId}/llm-token
```

#### Update LLM Token Configuration (Full)
```http
PUT /api/products/{productId}/llm-token
```

#### Update LLM Token Configuration (Partial)
```http
PATCH /api/products/{productId}/llm-token
```

#### Delete LLM Token Configuration
```http
DELETE /api/products/{productId}/llm-token
```

#### List All LLM Token Configurations
```http
GET /api/products/llm-token
```

### SQL Result Product Type

#### Create SQL Result Configuration
```http
POST /api/products/{productId}/sql-result
```

**Request Body:**
```json
{
  "maxResultSetSize": 10000,
  "supportedDatabases": ["PostgreSQL", "MySQL", "Oracle"],
  "queryTimeoutSeconds": 30,
  "cachingEnabled": true
}
```

#### Get SQL Result Configuration
```http
GET /api/products/{productId}/sql-result
```

#### Update SQL Result Configuration (Full)
```http
PUT /api/products/{productId}/sql-result
```

#### Update SQL Result Configuration (Partial)
```http
PATCH /api/products/{productId}/sql-result
```

#### Delete SQL Result Configuration
```http
DELETE /api/products/{productId}/sql-result
```

#### List All SQL Result Configurations
```http
GET /api/products/sql-result
```

### Storage Product Type

#### Create Storage Configuration
```http
POST /api/products/{productId}/storage
```

**Request Body:**
```json
{
  "storageType": "OBJECT_STORAGE",
  "maxStorageSize": "1TB",
  "replicationFactor": 3,
  "encryptionEnabled": true,
  "backupEnabled": true
}
```

#### Get Storage Configuration
```http
GET /api/products/{productId}/storage
```

#### Update Storage Configuration (Full)
```http
PUT /api/products/{productId}/storage
```

#### Update Storage Configuration (Partial)
```http
PATCH /api/products/{productId}/storage
```

#### Delete Storage Configuration
```http
DELETE /api/products/{productId}/storage
```

#### List All Storage Configurations
```http
GET /api/products/storage
```

---

## Rate Plan Management

### Create Rate Plan
```http
POST /api/rateplans
```

**Description:** Create a new rate plan for a product.

**Request Body:**
```json
{
  "ratePlanName": "Enterprise API Plan",
  "productId": 1,
  "description": "High-volume API access plan for enterprise customers",
  "billingFrequency": "MONTHLY",
  "paymentType": "PREPAID",
  "billableMetricId": 123
}
```

**Response:**
```json
{
  "ratePlanId": 1,
  "ratePlanName": "Enterprise API Plan",
  "description": "High-volume API access plan for enterprise customers",
  "billingFrequency": "MONTHLY",
  "productId": 1,
  "productName": "API Gateway Service",
  "paymentType": "PREPAID",
  "billableMetricId": 123,
  "status": "DRAFT",
  "createdOn": "2024-11-04T10:30:00",
  "lastUpdated": "2024-11-04T10:30:00"
}
```

### Get Rate Plan by ID
```http
GET /api/rateplans/{ratePlanId}
```

**Description:** Retrieve a specific rate plan by its ID.

**Path Parameters:**
- `ratePlanId` (Long): Rate plan ID

### List All Rate Plans
```http
GET /api/rateplans
```

**Description:** Retrieve all rate plans in the system.

### List Rate Plans by Product ID
```http
GET /api/rateplans/product/{productId}
```

**Description:** Retrieve all rate plans for a specific product.

**Path Parameters:**
- `productId` (Long): Product ID

### Update Rate Plan (Full)
```http
PUT /api/rateplans/{ratePlanId}
```

**Description:** Fully update a rate plan.

**Path Parameters:**
- `ratePlanId` (Long): Rate plan ID

### Update Rate Plan (Partial)
```http
PATCH /api/rateplans/{ratePlanId}
```

**Description:** Partially update a rate plan.

**Path Parameters:**
- `ratePlanId` (Long): Rate plan ID

### Confirm Rate Plan
```http
POST /api/rateplans/{ratePlanId}/confirm
```

**Description:** Confirm a rate plan, changing its status to ACTIVE.

**Path Parameters:**
- `ratePlanId` (Long): Rate plan ID

### Delete Rate Plan
```http
DELETE /api/rateplans/{ratePlanId}
```

**Description:** Delete a rate plan and all associated pricing configurations.

**Path Parameters:**
- `ratePlanId` (Long): Rate plan ID

**Response:** HTTP 204 No Content

### Delete Rate Plan by Billable Metric (Internal)
```http
DELETE /api/rateplans/internal/billable-metrics/{metricId}
```

**Description:** Internal endpoint called by Billable Metrics Service when a metric is deleted.

**Path Parameters:**
- `metricId` (Long): Billable metric ID

---

## Pricing Models

### Flat Fee Pricing

#### Create Flat Fee
```http
POST /api/rateplans/{ratePlanId}/flatfee
```

**Request Body:**
```json
{
  "amount": 99.99,
  "currency": "USD",
  "includedUnits": 1000,
  "overageRate": 0.01
}
```

#### Update Flat Fee
```http
PUT /api/rateplans/{ratePlanId}/flatfee/{flatFeeId}
```

#### Get Flat Fee by Rate Plan
```http
GET /api/rateplans/{ratePlanId}/flatfee
```

#### List All Flat Fees
```http
GET /api/rateplans/{ratePlanId}/flatfee/all
```

#### Delete Flat Fee
```http
DELETE /api/rateplans/{ratePlanId}/flatfee/{flatFeeId}
```

### Tiered Pricing

#### Create Tiered Pricing
```http
POST /api/rateplans/{ratePlanId}/tiered
```

**Request Body:**
```json
{
  "pricingName": "API Call Tiers",
  "currency": "USD",
  "tiers": [
    {
      "tierNumber": 1,
      "minUnits": 0,
      "maxUnits": 1000,
      "pricePerUnit": 0.10
    },
    {
      "tierNumber": 2,
      "minUnits": 1001,
      "maxUnits": 5000,
      "pricePerUnit": 0.08
    }
  ]
}
```

#### Update Tiered Pricing
```http
PUT /api/rateplans/{ratePlanId}/tiered/{tieredPricingId}
```

#### Get Tiered Pricing by Rate Plan
```http
GET /api/rateplans/{ratePlanId}/tiered
```

#### Get Tiered Pricing by ID
```http
GET /api/rateplans/{ratePlanId}/tiered/{tieredPricingId}
```

#### List All Tiered Pricing
```http
GET /api/rateplans/{ratePlanId}/tiered/all
```

#### Delete Tiered Pricing
```http
DELETE /api/rateplans/{ratePlanId}/tiered/{tieredPricingId}
```

### Stair Step Pricing

#### Create Stair Step Pricing
```http
POST /api/rateplans/{ratePlanId}/stairstep
```

**Request Body:**
```json
{
  "pricingName": "Volume Discounts",
  "currency": "USD",
  "steps": [
    {
      "stepNumber": 1,
      "usageThresholdStart": 0,
      "usageThresholdEnd": 1000,
      "monthlyCharge": 100.00
    },
    {
      "stepNumber": 2,
      "usageThresholdStart": 1001,
      "usageThresholdEnd": 5000,
      "monthlyCharge": 400.00
    }
  ]
}
```

#### Update Stair Step Pricing
```http
PUT /api/rateplans/{ratePlanId}/stairstep/{stairStepPricingId}
```

#### Get Stair Step Pricing by Rate Plan
```http
GET /api/rateplans/{ratePlanId}/stairstep
```

#### Get Stair Step Pricing by ID
```http
GET /api/rateplans/{ratePlanId}/stairstep/{stairStepPricingId}
```

#### List All Stair Step Pricing
```http
GET /api/rateplans/{ratePlanId}/stairstep/all
```

#### Delete Stair Step Pricing
```http
DELETE /api/rateplans/{ratePlanId}/stairstep/{stairStepPricingId}
```

### Volume Pricing

#### Create Volume Pricing
```http
POST /api/rateplans/{ratePlanId}/volume-pricing
```

**Request Body:**
```json
{
  "pricingName": "Bulk API Calls",
  "currency": "USD",
  "volumeTiers": [
    {
      "tierNumber": 1,
      "minVolume": 0,
      "maxVolume": 10000,
      "pricePerUnit": 0.05
    },
    {
      "tierNumber": 2,
      "minVolume": 10001,
      "maxVolume": 50000,
      "pricePerUnit": 0.03
    }
  ]
}
```

#### Update Volume Pricing
```http
PUT /api/rateplans/{ratePlanId}/volume-pricing/{volumePricingId}
```

#### Get Volume Pricing by ID
```http
GET /api/rateplans/{ratePlanId}/volume-pricing/{volumePricingId}
```

#### Get Volume Pricing by Rate Plan
```http
GET /api/rateplans/{ratePlanId}/volume-pricing
```

#### List All Volume Pricing
```http
GET /api/rateplans/{ratePlanId}/volume-pricing/all
```

#### Delete Volume Pricing
```http
DELETE /api/rateplans/{ratePlanId}/volume-pricing/{volumePricingId}
```

### Usage-Based Pricing

#### Create Usage-Based Pricing
```http
POST /api/rateplans/{ratePlanId}/usagebased
```

**Request Body:**
```json
{
  "pricingName": "Pay-per-Use",
  "currency": "USD",
  "pricePerUnit": 0.001,
  "minimumCharge": 5.00,
  "includedUnits": 0
}
```

#### Update Usage-Based Pricing
```http
PUT /api/rateplans/{ratePlanId}/usagebased/{usageBasedPricingId}
```

#### Get Usage-Based Pricing by Rate Plan
```http
GET /api/rateplans/{ratePlanId}/usagebased
```

#### Get Usage-Based Pricing by ID
```http
GET /api/rateplans/{ratePlanId}/usagebased/{usageBasedPricingId}
```

#### List All Usage-Based Pricing
```http
GET /api/rateplans/{ratePlanId}/usagebased/all
```

#### Delete Usage-Based Pricing
```http
DELETE /api/rateplans/{ratePlanId}/usagebased/{usageBasedPricingId}
```

---

## Additional Features

### Setup Fees

#### Create Setup Fee
```http
POST /api/rateplans/{ratePlanId}/setupfees
```

**Request Body:**
```json
{
  "feeName": "Account Setup",
  "amount": 50.00,
  "currency": "USD",
  "isOneTime": true,
  "description": "One-time account setup and configuration fee"
}
```

#### Update Setup Fee
```http
PUT /api/rateplans/{ratePlanId}/setupfees/{id}
```

#### Partial Update Setup Fee
```http
PATCH /api/rateplans/{ratePlanId}/setupfees/{id}
```

#### Get Setup Fees by Rate Plan
```http
GET /api/rateplans/{ratePlanId}/setupfees
```

#### Get Setup Fee by ID
```http
GET /api/rateplans/{ratePlanId}/setupfees/{id}
```

#### List All Setup Fees
```http
GET /api/rateplans/{ratePlanId}/setupfees/all
```

#### Delete Setup Fee
```http
DELETE /api/rateplans/{ratePlanId}/setupfees/{id}
```

### Discounts

#### Create Discount
```http
POST /api/rateplans/{ratePlanId}/discounts
```

**Request Body:**
```json
{
  "discountName": "Early Bird Discount",
  "discountType": "PERCENTAGE",
  "discountValue": 15.0,
  "validFrom": "2024-01-01T00:00:00",
  "validTo": "2024-12-31T23:59:59",
  "maxUsageCount": 100
}
```

#### Update Discount
```http
PUT /api/rateplans/{ratePlanId}/discounts/{id}
```

#### Partial Update Discount
```http
PATCH /api/rateplans/{ratePlanId}/discounts/{id}
```

#### Get Discounts by Rate Plan
```http
GET /api/rateplans/{ratePlanId}/discounts
```

#### Get Discount by ID
```http
GET /api/rateplans/{ratePlanId}/discounts/{id}
```

#### List All Discounts
```http
GET /api/rateplans/{ratePlanId}/discounts/all
```

#### Delete Discount
```http
DELETE /api/rateplans/{ratePlanId}/discounts/{id}
```

### Freemium Features

#### Create Freemium
```http
POST /api/rateplans/{ratePlanId}/freemiums
```

**Request Body:**
```json
{
  "featureName": "Free API Calls",
  "freeUnits": 1000,
  "resetPeriod": "MONTHLY",
  "description": "1000 free API calls per month for new users"
}
```

#### Update Freemium
```http
PUT /api/rateplans/{ratePlanId}/freemiums/{id}
```

#### Partial Update Freemium
```http
PATCH /api/rateplans/{ratePlanId}/freemiums/{id}
```

#### Get Freemiums by Rate Plan
```http
GET /api/rateplans/{ratePlanId}/freemiums
```

#### Get Freemium by ID
```http
GET /api/rateplans/{ratePlanId}/freemiums/{id}
```

#### List All Freemiums
```http
GET /api/rateplans/{ratePlanId}/freemiums/all
```

#### Delete Freemium
```http
DELETE /api/rateplans/{ratePlanId}/freemiums/{id}
```

### Minimum Commitments

#### Create Minimum Commitment
```http
POST /api/rateplans/{ratePlanId}/minimumcommitments
```

**Request Body:**
```json
{
  "commitmentName": "Annual Minimum",
  "minimumAmount": 1200.00,
  "currency": "USD",
  "commitmentPeriod": "ANNUAL",
  "penaltyRate": 0.1
}
```

#### Update Minimum Commitment
```http
PUT /api/rateplans/{ratePlanId}/minimumcommitments/{id}
```

#### Partial Update Minimum Commitment
```http
PATCH /api/rateplans/{ratePlanId}/minimumcommitments/{id}
```

#### Get Minimum Commitments by Rate Plan
```http
GET /api/rateplans/{ratePlanId}/minimumcommitments
```

#### Get Minimum Commitment by ID
```http
GET /api/rateplans/{ratePlanId}/minimumcommitments/{id}
```

#### List All Minimum Commitments
```http
GET /api/rateplans/{ratePlanId}/minimumcommitments/all
```

#### Delete Minimum Commitment
```http
DELETE /api/rateplans/{ratePlanId}/minimumcommitments/{id}
```

---

## Revenue Estimation

### Estimate Revenue
```http
POST /api/estimator
```

**Description:** Calculate detailed revenue estimates based on usage and pricing configuration.

**Request Body:**
```json
{
  "pricingModel": "TIERED",
  "usage": 2500,
  "tiers": [
    {
      "minUnits": 0,
      "maxUnits": 1000,
      "pricePerUnit": 0.10
    },
    {
      "minUnits": 1001,
      "maxUnits": 5000,
      "pricePerUnit": 0.08
    }
  ],
  "includeSetup": true,
  "setupFee": 50.00,
  "includeDiscount": true,
  "discountPct": 10.0,
  "includeFreemium": true,
  "freeUnits": 500,
  "includeCommitment": true,
  "minCommitmentAmount": 200.00
}
```

**Response:**
```json
{
  "totalEstimate": 340.00,
  "breakdown": {
    "baseCharge": 260.00,
    "setupFee": 50.00,
    "discount": -26.00,
    "freemiumSavings": -40.00,
    "minimumCommitmentAdjustment": 0.00
  },
  "details": {
    "usageAfterFreemium": 2000,
    "tierBreakdown": [
      {
        "tier": 1,
        "units": 1000,
        "rate": 0.10,
        "amount": 100.00
      },
      {
        "tier": 2,
        "units": 1000,
        "rate": 0.08,
        "amount": 80.00
      }
    ]
  },
  "currency": "USD"
}
```

---

## Error Handling

### Standard Error Response Format

```json
{
  "timestamp": "2024-11-04T10:30:00.000Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for field 'productName': Product name is required",
  "path": "/api/products"
}
```

### Common HTTP Status Codes

- **200 OK**: Successful GET, PUT, PATCH operations
- **201 Created**: Successful POST operations
- **204 No Content**: Successful DELETE operations
- **400 Bad Request**: Invalid request data or validation errors
- **401 Unauthorized**: Missing or invalid authentication
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource not found
- **409 Conflict**: Resource already exists or constraint violation
- **422 Unprocessable Entity**: Business logic validation errors
- **500 Internal Server Error**: Unexpected server errors

### Validation Errors

Field validation errors include detailed information about which fields failed validation:

```json
{
  "timestamp": "2024-11-04T10:30:00.000Z",
  "status": 400,
  "error": "Validation Failed",
  "message": "Request validation failed",
  "fieldErrors": [
    {
      "field": "productName",
      "rejectedValue": "",
      "message": "Product name is required"
    },
    {
      "field": "internalSkuCode",
      "rejectedValue": null,
      "message": "SKU code is required"
    }
  ],
  "path": "/api/products"
}
```

---

## Data Models

### Product Status Enum
- `DRAFT`: Product is being created/configured
- `ACTIVE`: Product is live and available
- `INACTIVE`: Product is temporarily disabled
- `DEPRECATED`: Product is no longer supported

### Product Type Enum
- `API`: API-based product
- `FLAT_FILE`: File processing product
- `LLM_TOKEN`: Language model token-based product
- `SQL_RESULT`: Database query result product
- `STORAGE`: Storage-based product

### Rate Plan Status Enum
- `DRAFT`: Rate plan is being configured
- `ACTIVE`: Rate plan is live and can be used
- `INACTIVE`: Rate plan is temporarily disabled

### Billing Frequency Enum
- `MONTHLY`: Monthly billing cycle
- `QUARTERLY`: Quarterly billing cycle
- `ANNUALLY`: Annual billing cycle
- `USAGE_BASED`: Billing based on actual usage

### Payment Type Enum
- `PREPAID`: Payment before service usage
- `POSTPAID`: Payment after service usage

### Rate Plan Type Enum (for estimation)
- `FLAT_FEE`: Fixed monthly/periodic fee
- `TIERED`: Progressive pricing tiers
- `VOLUME`: Volume-based pricing
- `USAGE_BASED`: Pay-per-use pricing
- `STAIR_STEP`: Step-function pricing

---

## Notes

1. **CORS Configuration**: The service is configured to accept requests from specific origins. Update CORS settings in application.yml for production use.

2. **File Upload Limits**: Maximum file size for icon uploads is 5MB.

3. **Database Support**: The service supports both PostgreSQL (default) and MySQL databases.

4. **Swagger Documentation**: Interactive API documentation is available at `/swagger-ui.html` when the service is running.

5. **Rate Limiting**: Consider implementing rate limiting for production deployments.

6. **Monitoring**: Use the `/api/health` endpoint for health checks and monitoring.

7. **Versioning**: This API follows RESTful conventions. Future versions may introduce versioning in the URL path.

---

*Last Updated: November 4, 2024*
*Service Version: 1.0.0*
