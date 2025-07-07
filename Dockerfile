# Use OpenJDK 21 base image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the application JAR built by Maven (located under target/)
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose port
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
