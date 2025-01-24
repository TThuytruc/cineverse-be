FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

# Copy the entire project
COPY . .

# Copy environment variables (if needed)
COPY .env .env

RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=optional:classpath:/,optional:file:config/,optional:file:.env"]