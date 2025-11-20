# AWS Deployment Configuration Summary

## Changes Made for AWS Deployment

### 1. **application.yml** - Updated Configuration

#### Port Changes
- **Server Port**: Changed from `8081` → `8080` (matches Docker and AWS setup)

#### Service URLs Updated
- **Customer Service**: `http://44.201.19.187:8081` (updated from old URL)
- **Billable Metrics Service**: `http://34.238.49.158:8081` (updated from old URL)
- **Kong/Apigee Gateway**: `http://44.203.209.2:8086` (reference only, not in config)

#### CORS Configuration
- **Removed**: `localhost:3000` and old AWS URLs
- **Updated Origins**: 
  - `http://13.115.248.133`
  - `http://54.221.164.5`

#### Cache Configuration
- Added `spring.cache.type: simple` to disable Redis and use in-memory cache

#### PostgreSQL Profile Settings
- **Port**: `5433` (for Docker deployment)
- **Liquibase**: Disabled (`enabled: false`) for AWS deployment
- **Hibernate DDL**: Set to `update` mode
- **Timezone**: Changed to `UTC` (from Asia/Kolkata)
- **JDBC Timezone**: Set to `UTC` for proper timestamp handling

#### Database Profiles
- **postgres**: Main profile for AWS deployment
- **h2**: Added for testing purposes
- **mysql**: Available but disabled by default

---

### 2. **docker-compose.yml** - Updated Configuration

#### PostgreSQL Container
- Added timezone environment variables:
  - `TZ: UTC`
  - `PGTZ: UTC`

#### Application Container
- **Port Mapping**: Changed from `8081:8081` → `8080:8080`
- **Added Environment Variables**:
  - `SPRING_CACHE_TYPE=simple`
  - `SPRING_AUTOCONFIGURE_EXCLUDE=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration`

---

### 3. **Dockerfile** - Simplified

```dockerfile
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

- Removed alpine variant for better compatibility
- Simplified structure
- Exposes port `8080`

---

## Deployment Verification

### Testing in AWS
When deployed to AWS, the application will:
1. ✅ Run on port `8080`
2. ✅ Connect to PostgreSQL database (internal Docker network)
3. ✅ Use UTC timezone for all timestamps
4. ✅ Communicate with Customer Service at `http://44.201.19.187:8081`
5. ✅ Communicate with Billable Metrics Service at `http://34.238.49.158:8081`
6. ✅ Accept requests from allowed CORS origins only
7. ✅ Use simple in-memory cache (no Redis dependency)

### Kong/Apigee Integration
- Gateway URL: `http://44.203.209.2:8086/swagger-ui/index.html`
- All previous Kong/Apigee configurations remain compatible

---

## Key Configuration Points

### For AWS Deployment:
- **Active Profile**: `postgres`
- **Port**: `8080`
- **Database**: PostgreSQL (via Docker internal network)
- **Liquibase**: Disabled (schema managed separately)
- **Timezone**: UTC
- **Cache**: Simple (in-memory)
- **CORS**: Only AWS origins allowed (no localhost)

### Java Configuration Files Updated:
- **WebConfig.java**: Removed all localhost origins, only AWS IPs allowed
- **SecurityConfig.java**: Default fallback changed from localhost to AWS origins

### For Local Development:
- Change PostgreSQL URL port to match your local setup
- Can enable Liquibase if needed for schema migrations
- **Important**: Add localhost origins back to CORS configuration if testing locally

---

## Environment Variables (for AWS/Docker)

The following environment variables are set in docker-compose.yml:
```yaml
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/productrateplanservice
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=P4ssword!
SPRING_CACHE_TYPE=simple
SPRING_AUTOCONFIGURE_EXCLUDE=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration
```

---

## Deployment Steps

1. **Build the application**:
   ```bash
   ./mvnw clean package -DskipTests
   cp target/*.jar app.jar
   ```

2. **Build and run with Docker Compose**:
   ```bash
   docker-compose down
   docker-compose up --build -d
   ```

3. **Verify deployment**:
   ```bash
   curl http://localhost:8080/swagger-ui/index.html
   ```

4. **Push to AWS** (via your CI/CD pipeline or manual deployment)

---

## Notes

- ⚠️ **No localhost URLs** in production configuration
- ⚠️ **Port 8080** is the standard for AWS deployment
- ⚠️ **UTC timezone** ensures consistency across services
- ⚠️ **Liquibase disabled** in AWS to prevent automatic schema changes
- ✅ **All service URLs updated** to current AWS instances
- ✅ **Cache configuration** prevents Redis dependency issues
