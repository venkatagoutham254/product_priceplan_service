# Use OpenJDK 21 base image
# ---------- Build stage ----------
FROM maven:3.9.7-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src src
RUN mvn -B clean package -DskipTests

# ---------- Runtime stage ----------
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR produced by Maven (e.g. target/product-priceplan-service-0.0.1-SNAPSHOT.jar)
# The build argument allows overriding the jar path if necessary.
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose port
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
