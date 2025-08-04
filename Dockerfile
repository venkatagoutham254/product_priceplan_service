FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the JAR file
COPY target/app.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
