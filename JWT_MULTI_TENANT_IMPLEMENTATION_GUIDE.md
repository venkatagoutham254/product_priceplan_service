# 🔐 JWT Multi-Tenant Authentication Implementation Guide

## 📋 Overview

This guide covers implementing JWT-based authentication with multi-tenancy for:
1. **ProductRatePlanService (Port 8081)** - Re-enable JWT for GET endpoints
2. **Integration Service (Port 8086)** - Add JWT authentication
3. **Frontend** - Handle JWT tokens

---

## 🎯 Part 1: ProductRatePlanService - Re-enable JWT Authentication

### **Step 1: Update SecurityConfig.java**

**File:** `/src/main/java/aforo/productrateplanservice/config/SecurityConfig.java`

```java
package aforo.productrateplanservice.config;

import aforo.productrateplanservice.security.JwtTenantFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${aforo.jwt.secret}")
    private String jwtSecret;

    @Value("${aforo.cors.allowed-origins:}")
    private String corsAllowedOrigins;

    @Value("${aforo.cors.allowed-methods:GET,POST,PUT,PATCH,DELETE,OPTIONS}")
    private String corsAllowedMethods;

    @Value("${aforo.cors.allowed-headers:Authorization,Content-Type,X-Organization-Id}")
    private String corsAllowedHeaders;

    @Value("${aforo.cors.allow-credentials:true}")
    private boolean corsAllowCredentials;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtTenantFilter jwtTenantFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .authorizeHttpRequests(auth -> auth
                // Allow Swagger & health endpoints without auth
                .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/api/health").permitAll()
                
                // ✅ KEEP: Allow import endpoint for external integrations (service-to-service)
                .requestMatchers(HttpMethod.POST, "/api/products/import").permitAll()
                
                // ✅ CHANGE: Require JWT for GET endpoints (user access)
                .requestMatchers(HttpMethod.GET, "/api/products/**").authenticated()
                
                // Allow preflight requests
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // All product endpoints require authentication
                .requestMatchers(HttpMethod.POST, "/api/products/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/products/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/products/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").authenticated()
                
                // Rate plan endpoints
                .requestMatchers(HttpMethod.POST, "/api/product-rate-plans/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/product-rate-plans/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/product-rate-plans/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/product-rate-plans/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/product-rate-plans/**").authenticated()
                
                // Everything else requires authentication
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt -> jwt.decoder(jwtDecoder()))
            );

        // Ensure the tenant filter runs AFTER Bearer token authentication
        http.addFilterAfter(jwtTenantFilter, BearerTokenAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(
                new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256")
        ).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(corsAllowCredentials);

        List<String> originPatterns;
        if (corsAllowedOrigins == null || corsAllowedOrigins.isBlank()) {
            originPatterns = List.of("http://localhost:3000");
        } else {
            originPatterns = Arrays.stream(corsAllowedOrigins.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }
        config.setAllowedOriginPatterns(originPatterns);

        List<String> methods = Arrays.stream(corsAllowedMethods.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        config.setAllowedMethods(methods);

        List<String> headers = Arrays.stream(corsAllowedHeaders.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        config.setAllowedHeaders(headers);

        config.setExposedHeaders(List.of("Location"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

**Key Changes:**
- ✅ **Import endpoint remains permitAll** (for service-to-service communication)
- ✅ **GET endpoints now require JWT** (for user access)
- ✅ Multi-tenancy via `JwtTenantFilter` extracts organizationId from JWT

---

## 🎯 Part 2: Integration Service - Add JWT Authentication

### **Step 1: Add Dependencies (pom.xml)**

```xml
<dependencies>
    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- JWT Support -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
    </dependency>
    
    <!-- Existing dependencies... -->
</dependencies>
```

---

### **Step 2: Create JwtTenantFilter.java**

**File:** `/src/main/java/com/aforo/apigee/security/JwtTenantFilter.java`

```java
package com.aforo.apigee.security;

import com.aforo.apigee.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtTenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        try {
            // Get authentication from security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
                Jwt jwt = (Jwt) authentication.getPrincipal();
                
                // Extract organizationId from JWT claims
                Long organizationId = jwt.getClaim("organizationId");
                
                if (organizationId != null) {
                    TenantContext.setCurrentTenant(organizationId);
                    log.debug("Set tenant context to organizationId: {}", organizationId);
                } else {
                    log.warn("JWT token does not contain organizationId claim");
                }
            }
            
            filterChain.doFilter(request, response);
            
        } finally {
            // Clear tenant context after request
            TenantContext.clear();
        }
    }
}
```

---

### **Step 3: Create TenantContext.java**

**File:** `/src/main/java/com/aforo/apigee/tenant/TenantContext.java`

```java
package com.aforo.apigee.tenant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TenantContext {
    
    private static final ThreadLocal<Long> currentTenant = new ThreadLocal<>();
    
    public static void setCurrentTenant(Long organizationId) {
        currentTenant.set(organizationId);
        log.debug("Tenant context set to: {}", organizationId);
    }
    
    public static Long getCurrentTenant() {
        Long tenant = currentTenant.get();
        if (tenant == null) {
            log.warn("No tenant context found in current thread");
        }
        return tenant;
    }
    
    public static void clear() {
        currentTenant.remove();
    }
}
```

---

### **Step 4: Create SecurityConfig.java**

**File:** `/src/main/java/com/aforo/apigee/config/SecurityConfig.java`

```java
package com.aforo.apigee.config;

import com.aforo.apigee.security.JwtTenantFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${aforo.jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtTenantFilter jwtTenantFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .authorizeHttpRequests(auth -> auth
                // Allow Swagger & health endpoints
                .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/api/health").permitAll()
                
                // Allow preflight requests
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // All integration endpoints require authentication
                .requestMatchers("/api/integrations/**").authenticated()
                
                // Everything else requires authentication
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt -> jwt.decoder(jwtDecoder()))
            );

        // Add tenant filter after JWT authentication
        http.addFilterAfter(jwtTenantFilter, BearerTokenAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(
                new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256")
        ).build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of("http://localhost:3000", "https://*.aforo.com"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Organization-Id"));
        config.setExposedHeaders(List.of("Location"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

---

### **Step 5: Update ApigeeIntegrationController.java**

**File:** `/src/main/java/com/aforo/apigee/controller/ApigeeIntegrationController.java`

```java
package com.aforo.apigee.controller;

import com.aforo.apigee.dto.ProductImportResponse;
import com.aforo.apigee.dto.SyncResponse;
import com.aforo.apigee.model.ApigeeProduct;
import com.aforo.apigee.service.ApigeeService;
import com.aforo.apigee.service.AforoProductService;
import com.aforo.apigee.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/integrations/apigee")
@RequiredArgsConstructor
@Slf4j
public class ApigeeIntegrationController {
    
    private final ApigeeService apigeeService;
    private final AforoProductService aforoProductService;
    
    @GetMapping("/products")
    public ResponseEntity<List<ApigeeProduct>> getProducts(Authentication authentication) {
        Long organizationId = TenantContext.getCurrentTenant();
        log.info("Fetching products from Apigee for organization: {}", organizationId);
        
        List<ApigeeProduct> products = apigeeService.fetchAndSaveProducts();
        return ResponseEntity.ok(products);
    }
    
    @PostMapping("/sync")
    public ResponseEntity<SyncResponse> syncProductsToAforo(Authentication authentication) {
        // Get organizationId from JWT token (via TenantContext)
        Long organizationId = TenantContext.getCurrentTenant();
        
        if (organizationId == null) {
            log.error("No organization ID found in JWT token");
            return ResponseEntity.badRequest().body(
                SyncResponse.builder()
                    .message("Missing organization ID in token")
                    .failed(0)
                    .build()
            );
        }
        
        log.info("Starting product sync from Apigee to Aforo for organization: {}", organizationId);
        
        try {
            // Fetch products from Apigee
            List<ApigeeProduct> apigeeProducts = apigeeService.fetchAndSaveProducts();
            log.info("Fetched {} products from Apigee", apigeeProducts.size());
            
            int created = 0;
            int updated = 0;
            int failed = 0;
            
            // Push each product to Aforo
            for (ApigeeProduct product : apigeeProducts) {
                try {
                    ProductImportResponse response = aforoProductService.pushProductToAforo(
                        product, 
                        organizationId
                    );
                    
                    if ("CREATED".equals(response.getStatus())) {
                        created++;
                    } else if ("UPDATED".equals(response.getStatus())) {
                        updated++;
                    }
                    
                } catch (Exception e) {
                    log.error("Failed to sync product {}: {}", product.getName(), e.getMessage());
                    failed++;
                }
            }
            
            SyncResponse syncResponse = SyncResponse.builder()
                .productsImported(created)
                .productsUpdated(updated)
                .totalSynced(created + updated)
                .failed(failed)
                .message(String.format("Sync completed: %d created, %d updated, %d failed", 
                                      created, updated, failed))
                .build();
            
            log.info("Sync completed: {} created, {} updated, {} failed", created, updated, failed);
            
            return ResponseEntity.ok(syncResponse);
            
        } catch (Exception e) {
            log.error("Sync failed: {}", e.getMessage());
            
            SyncResponse errorResponse = SyncResponse.builder()
                .productsImported(0)
                .productsUpdated(0)
                .totalSynced(0)
                .failed(0)
                .message("Sync failed: " + e.getMessage())
                .build();
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
```

---

### **Step 6: Add Configuration (application.yml)**

```yaml
aforo:
  jwt:
    secret: ${JWT_SECRET:your-secret-key-min-32-characters-long}
  product:
    service:
      url: http://localhost:8081
  cors:
    allowed-origins: http://localhost:3000,https://*.aforo.com
    allowed-methods: GET,POST,PUT,PATCH,DELETE,OPTIONS
    allowed-headers: Authorization,Content-Type,X-Organization-Id
    allow-credentials: true
```

---

## 🎯 Part 3: Frontend Implementation

### **Step 1: Store JWT Token**

```javascript
// After login
const login = async (username, password) => {
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });
  
  const data = await response.json();
  
  // Store JWT token
  localStorage.setItem('token', data.token);
  localStorage.setItem('organizationId', data.organizationId);
};
```

---

### **Step 2: Use JWT Token in API Calls**

```javascript
// Get all products (now requires JWT)
const getProducts = async () => {
  const token = localStorage.getItem('token');
  const organizationId = localStorage.getItem('organizationId');
  
  const response = await fetch('http://localhost:8081/api/products', {
    headers: {
      'Authorization': `Bearer ${token}`,
      'X-Organization-Id': organizationId
    }
  });
  
  return await response.json();
};

// Sync from Apigee (requires JWT)
const syncFromApigee = async () => {
  const token = localStorage.getItem('token');
  
  const response = await fetch('http://localhost:8086/api/integrations/apigee/sync', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  return await response.json();
};
```

---

### **Step 3: Complete React Example**

```javascript
import React, { useState, useEffect } from 'react';

function ProductsPage() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [syncing, setSyncing] = useState(false);

  useEffect(() => {
    loadProducts();
  }, []);

  const loadProducts = async () => {
    setLoading(true);
    try {
      const token = localStorage.getItem('token');
      const organizationId = localStorage.getItem('organizationId');
      
      const response = await fetch('http://localhost:8081/api/products', {
        headers: {
          'Authorization': `Bearer ${token}`,
          'X-Organization-Id': organizationId
        }
      });
      
      if (response.status === 401) {
        // Token expired or invalid
        window.location.href = '/login';
        return;
      }
      
      const data = await response.json();
      setProducts(data);
    } catch (error) {
      console.error('Failed to load products:', error);
      alert('Failed to load products');
    } finally {
      setLoading(false);
    }
  };

  const syncFromApigee = async () => {
    setSyncing(true);
    try {
      const token = localStorage.getItem('token');
      
      const response = await fetch('http://localhost:8086/api/integrations/apigee/sync', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      
      if (response.status === 401) {
        window.location.href = '/login';
        return;
      }
      
      const result = await response.json();
      alert(`✅ Synced ${result.totalSynced} products from Apigee!`);
      loadProducts(); // Refresh list
    } catch (error) {
      console.error('Sync failed:', error);
      alert('❌ Sync failed: ' + error.message);
    } finally {
      setSyncing(false);
    }
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between' }}>
        <h2>Products</h2>
        <button onClick={syncFromApigee} disabled={syncing}>
          {syncing ? '⏳ Syncing...' : '🔄 Sync from Apigee'}
        </button>
      </div>

      {loading ? (
        <p>Loading products...</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Name</th>
              <th>Source</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {products.map(product => (
              <tr key={product.productId}>
                <td>{product.productName}</td>
                <td>
                  {product.source === 'APIGEE' && '🔗 Apigee'}
                  {product.source === 'MANUAL' && '✏️ Manual'}
                </td>
                <td>{product.status}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default ProductsPage;
```

---

## 🧪 Testing with JWT

### **Test 1: Get JWT Token (Mock)**

For testing, create a simple JWT token:

```bash
# Install jwt-cli: brew install jwt-cli (Mac) or npm install -g jwt-cli

# Create JWT token
jwt encode --secret "your-secret-key-min-32-characters-long" \
  '{"sub": "user@example.com", "organizationId": 1, "exp": 1735689600}'
```

---

### **Test 2: Test ProductRatePlanService with JWT**

```bash
# Get products (now requires JWT)
TOKEN="your-jwt-token-here"

curl -X GET http://localhost:8081/api/products \
  -H "Authorization: Bearer $TOKEN" \
  -H "X-Organization-Id: 1"
```

---

### **Test 3: Test Integration Service with JWT**

```bash
# Sync products (now requires JWT)
TOKEN="your-jwt-token-here"

curl -X POST http://localhost:8086/api/integrations/apigee/sync \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📊 JWT Token Structure

Your JWT should contain:

```json
{
  "sub": "user@example.com",
  "organizationId": 1,
  "roles": ["USER", "ADMIN"],
  "iat": 1699123456,
  "exp": 1735689600
}
```

**Required Claims:**
- `organizationId` - For multi-tenancy
- `sub` - User identifier
- `exp` - Expiration time

---

## 🔒 Security Summary

### **ProductRatePlanService (Port 8081):**
- ✅ Import endpoint: **No JWT** (service-to-service)
- ✅ GET endpoints: **Requires JWT** (user access)
- ✅ POST/PUT/DELETE: **Requires JWT** (user access)

### **Integration Service (Port 8086):**
- ✅ All endpoints: **Requires JWT** (user access)
- ✅ Service-to-service calls to ProductRatePlanService: **No JWT needed**

### **Multi-Tenancy:**
- ✅ `organizationId` extracted from JWT token
- ✅ Stored in `TenantContext` (ThreadLocal)
- ✅ Used for data isolation

---

## ✅ Summary

**What This Implements:**
1. ✅ JWT authentication for both services
2. ✅ Multi-tenancy via organizationId in JWT
3. ✅ Service-to-service communication (import endpoint remains open)
4. ✅ User-facing endpoints require JWT
5. ✅ Frontend integration with JWT tokens

**Next Steps:**
1. Apply changes to ProductRatePlanService
2. Apply changes to Integration Service
3. Update Frontend to use JWT tokens
4. Test end-to-end flow

---

**All code is ready to implement!** ✅
