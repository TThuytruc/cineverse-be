FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/cineverse_be-0.0.1-SNAPSHOT.jar cineverse_be.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "cineverse_be.jar"]