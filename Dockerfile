# Multi-stage build для оптимизации размера образа

# Stage 1: Build stage
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Копируем pom.xml и загружаем зависимости (кэширование слоев)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходный код и собираем приложение
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Создаем непривилегированного пользователя для безопасности
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Копируем JAR файл из build stage
COPY --from=build /app/target/*.jar app.jar

# Открываем порт приложения
EXPOSE 8080

# Healthcheck для проверки работоспособности приложения
# Примечание: Убедитесь, что в приложении настроен actuator или измените endpoint
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/swagger-ui.html || exit 1

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]

