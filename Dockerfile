FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/esprit-4se2-pi-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]