# ✅ JAR Manifest Error FIXED!

## The Problem

Your Docker logs showed:
```
no main manifest attribute, in app.jar
```

This meant the JAR file didn't have the proper manifest to be executable.

---

## What I Fixed

### ✅ 1. Fixed pom.xml (PUSHED TO GITHUB)

**Added Spring Boot repackage goal:**
```xml
<build>
    <finalName>app</finalName>  <!-- Moved here from plugin config -->
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <mainClass>aforo.productrateplanservice.ProductRatePlanServiceApplication</mainClass>
                <profiles>
                    <profile>postgres</profile>
                </profiles>
                <excludes>
                    <exclude>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </exclude>
                </excludes>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>repackage</goal>  <!-- THIS WAS MISSING! -->
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

**What this does:**
- The `repackage` goal creates an executable JAR with proper MANIFEST.MF
- Adds `Main-Class: org.springframework.boot.loader.launch.JarLauncher`
- Adds `Start-Class: aforo.productrateplanservice.ProductRatePlanServiceApplication`

### ⚠️ 2. Fixed Workflow File (NEEDS MANUAL UPDATE)

The workflow needs one small change:

**Line 25 - Change FROM:**
```yaml
cp target/productrateplanservie-0.0.1-SNAPSHOT.jar app.jar
```

**TO:**
```yaml
cp target/app.jar app.jar
```

**Why:** Because `<finalName>app</finalName>` means Maven creates `app.jar` directly in the target folder.

---

## How to Update Workflow on GitHub

### Option 1: Edit on GitHub (EASIEST)

1. Go to: https://github.com/venkatagoutham254/product_priceplan_service/blob/main/.github/workflows/ci-cd.yml
2. Click the pencil icon (Edit)
3. **Line 25**: Change to: `cp target/app.jar app.jar`
4. **Line 48**: Change `/api/health` to `/actuator/health`
5. **Line 50**: Change `/api/health` to `/actuator/health`
6. Commit directly to main

### Option 2: Use Git with Your Credentials

Open a **new terminal** (not VS Code) and run:
```bash
cd "/Users/venkatagowtham/Desktop/Product and rateplan microservice/product_priceplan_service"
git pull origin main
git add .github/workflows/ci-cd.yml
git commit -m "Fix workflow JAR path and health check endpoint"
git push origin main
```

---

## Verification

I tested the build locally and verified the JAR has the correct manifest:

```
✅ Main-Class: org.springframework.boot.loader.launch.JarLauncher
✅ Start-Class: aforo.productrateplanservice.ProductRatePlanServiceApplication
✅ Spring-Boot-Version: 3.3.4
✅ Spring-Boot-Classes: BOOT-INF/classes/
✅ Spring-Boot-Lib: BOOT-INF/lib/
```

---

## After You Update the Workflow

The deployment will:
1. ✅ Build executable JAR with proper manifest
2. ✅ Copy correct JAR file (app.jar)
3. ✅ Deploy to EC2
4. ✅ Docker will start successfully (no more "no main manifest attribute" error)
5. ✅ Health check will pass
6. ✅ Application will be available!

---

## Test Your Application

**Kong/Apigee Gateway:**
```
http://44.203.209.2:8086/swagger-ui/index.html
```

**Health Check:**
```bash
curl http://YOUR_EC2_IP:8080/actuator/health
```

Expected response:
```json
{"status":"UP"}
```

---

## Summary

✅ **pom.xml**: Fixed and pushed to GitHub  
⚠️ **Workflow file**: Needs manual update (3 lines to change)  
✅ **JAR manifest**: Now correct with Main-Class attribute  
✅ **Port 8080**: Configured everywhere  
✅ **Actuator health**: Configured at `/actuator/health`  

**Just update the workflow file on GitHub and your deployment will work perfectly!** 🚀

---

## Quick Reference - Changes Needed in Workflow

```yaml
# Line 25 - Change this:
cp target/productrateplanservie-0.0.1-SNAPSHOT.jar app.jar
# To this:
cp target/app.jar app.jar

# Line 48 - Change this:
echo "Waiting for http://${{ secrets.EC2_HOST }}:8080/api/health ..."
# To this:
echo "Waiting for http://${{ secrets.EC2_HOST }}:8080/actuator/health ..."

# Line 50 - Change this:
if curl -fs http://${{ secrets.EC2_HOST }}:8080/api/health > /dev/null; then
# To this:
if curl -fs http://${{ secrets.EC2_HOST }}:8080/actuator/health > /dev/null; then
```

That's it! 3 simple changes and everything will work! 🎉
