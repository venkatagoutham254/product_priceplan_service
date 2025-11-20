# Pre-Deployment Checklist ✅

## Configuration Files Verification

### ✅ application.yml
- [x] Server port: `8080`
- [x] Customer service URL: `http://44.201.19.187:8081`
- [x] Billable metrics URL: `http://34.238.49.158:8081`
- [x] CORS origins: Only AWS IPs (no localhost)
- [x] Cache type: `simple`
- [x] PostgreSQL port: `5433`
- [x] Liquibase: Disabled for postgres profile
- [x] Timezone: UTC

### ✅ docker-compose.yml
- [x] App port mapping: `8080:8080`
- [x] PostgreSQL timezone: UTC
- [x] Cache environment variable set
- [x] Redis autoconfigure excluded

### ✅ Dockerfile
- [x] Base image: `eclipse-temurin:21-jdk`
- [x] Exposes port: `8080`
- [x] Simplified structure

### ✅ Java Configuration Files

#### WebConfig.java
- [x] CORS origins: Only AWS IPs
- [x] No localhost URLs
- [x] Methods match application.yml

#### SecurityConfig.java
- [x] Default CORS: AWS origins
- [x] No localhost fallback

#### WebClientConfig.java
- [x] Customer service: Uses `${customer.service.url}` property
- [x] Billable metrics: Uses `${billableMetrics.service.url}` property
- [x] No hardcoded old URLs

---

## Service URLs Verification

| Service | Configuration | Status |
|---------|--------------|--------|
| Customer Service | `http://44.201.19.187:8081` | ✅ Updated |
| Billable Metrics | `http://34.238.49.158:8081` | ✅ Updated |
| Kong/Apigee Gateway | `http://44.203.209.2:8086` | ✅ Reference |

---

## CORS Configuration Verification

### Allowed Origins:
- ✅ `http://13.115.248.133`
- ✅ `http://54.221.164.5`

### Removed Origins:
- ❌ `localhost:3000` through `localhost:3005` (removed)
- ❌ Old S3 website URL (removed)
- ❌ Old AWS IPs (removed)

---

## Port Configuration Verification

| File | Configuration | Value |
|------|--------------|-------|
| application.yml | `server.port` | `8080` ✅ |
| docker-compose.yml | Port mapping | `8080:8080` ✅ |
| Dockerfile | EXPOSE | `8080` ✅ |

---

## Database Configuration Verification

### PostgreSQL Settings:
- [x] Port: `5433` (host) → `5432` (container)
- [x] Database: `productrateplanservice`
- [x] Username: `root`
- [x] Password: `P4ssword!`
- [x] Timezone: UTC
- [x] Hibernate DDL: `update`
- [x] Liquibase: Disabled

---

## Timezone Configuration Verification

| Configuration | Setting | Status |
|--------------|---------|--------|
| application.yml (Jackson) | `UTC` | ✅ |
| application.yml (Hibernate) | `UTC` | ✅ |
| docker-compose.yml (TZ) | `UTC` | ✅ |
| docker-compose.yml (PGTZ) | `UTC` | ✅ |

---

## Cache Configuration Verification

- [x] `spring.cache.type: simple` in application.yml
- [x] `SPRING_CACHE_TYPE=simple` in docker-compose.yml
- [x] Redis autoconfigure excluded in docker-compose.yml

---

## No Localhost References

Verified files for localhost references:
- [x] application.yml - Clean ✅
- [x] docker-compose.yml - Clean ✅
- [x] WebConfig.java - Clean ✅
- [x] SecurityConfig.java - Clean ✅
- [x] WebClientConfig.java - Clean ✅

---

## Deployment Steps

### 1. Build Application
```bash
./mvnw clean package -DskipTests
```

### 2. Copy JAR
```bash
cp target/*.jar app.jar
```

### 3. Build Docker Image
```bash
docker-compose build
```

### 4. Start Services
```bash
docker-compose up -d
```

### 5. Verify Deployment
```bash
# Check if containers are running
docker ps

# Check application logs
docker logs productrateplanservice -f

# Test Swagger UI
curl http://localhost:8080/swagger-ui/index.html

# Test health endpoint (if available)
curl http://localhost:8080/actuator/health
```

### 6. Test Service Communication
```bash
# Test from within the container
docker exec -it productrateplanservice curl http://44.201.19.187:8081/swagger-ui/index.html
docker exec -it productrateplanservice curl http://34.238.49.158:8081/swagger-ui/index.html
```

---

## AWS Deployment Verification

Once deployed to AWS, verify:

1. **Application Access**
   - [ ] Kong/Apigee gateway accessible: `http://44.203.209.2:8086/swagger-ui/index.html`
   - [ ] Swagger UI loads correctly
   - [ ] API endpoints respond

2. **Service Communication**
   - [ ] Can communicate with Customer Service
   - [ ] Can communicate with Billable Metrics Service
   - [ ] No connection errors in logs

3. **CORS Verification**
   - [ ] Frontend from `13.115.248.133` can access API
   - [ ] Frontend from `54.221.164.5` can access API
   - [ ] Localhost requests are blocked (as expected)

4. **Database Connection**
   - [ ] Application connects to PostgreSQL
   - [ ] No database connection errors
   - [ ] Queries execute successfully

5. **Timezone Verification**
   - [ ] Timestamps are in UTC
   - [ ] Date formatting is correct
   - [ ] No timezone conversion issues

---

## Rollback Plan

If deployment fails:

1. **Check logs**:
   ```bash
   docker logs productrateplanservice --tail 100
   ```

2. **Common issues**:
   - Port already in use → Stop conflicting service
   - Database connection failed → Check PostgreSQL container
   - Service URLs unreachable → Verify AWS service status
   - CORS errors → Check frontend origin matches configuration

3. **Rollback**:
   ```bash
   docker-compose down
   # Fix configuration
   docker-compose up --build -d
   ```

---

## Final Checklist Before Push to AWS

- [x] All configuration files updated
- [x] No localhost URLs in any file
- [x] Service URLs point to current AWS instances
- [x] Port 8080 configured everywhere
- [x] Timezone set to UTC
- [x] Cache configured as simple
- [x] Liquibase disabled
- [x] CORS restricted to AWS origins
- [x] Docker configuration matches requirements
- [x] Java configuration files updated
- [x] Documentation created

---

## 🎉 Ready for Deployment!

All configurations have been verified and are consistent. The application is ready to be deployed to AWS.

**Next Steps:**
1. Commit all changes to Git
2. Push to your deployment branch
3. Trigger your CI/CD pipeline (or deploy manually)
4. Monitor deployment logs
5. Verify application functionality in AWS

**Important Reminder:**
- This configuration is for **AWS production deployment**
- For **local testing**, you may need to temporarily add localhost to CORS
- Keep this documentation for future reference
