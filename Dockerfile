FROM openjdk:21-jdk-slim

WORKDIR /app

# Use actual name of the jar built by Maven
COPY target/productrateplanservie-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
