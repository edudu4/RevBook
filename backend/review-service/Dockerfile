# Build a partir da raiz de backend/ (contexto = backend/), pois este módulo
# herda do POM pai do reactor multi-módulo.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY . .
RUN mvn -B -pl review-service -am -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/review-service/target/review-service-*.jar app.jar
EXPOSE 8878
ENTRYPOINT ["java", "-jar", "app.jar"]
