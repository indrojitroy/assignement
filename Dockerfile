FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY . .
RUN mvn clean package

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/assignment-*.jar /app/app.jar

COPY src/main/resources/client.truststore.jks /app/client.truststore.jks
ENV TRUSTSTORE_PATH=/app/

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]