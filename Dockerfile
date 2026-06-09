# === ETAPA 1: Compilación ===
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests

# === ETAPA 2: Ejecución ===
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto nativo de Order Service
EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]