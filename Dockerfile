FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy app.jar (already renamed by GitHub Actions)
COPY target/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]