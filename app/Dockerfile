# Этап 1: Сборка (используем локальный образ Maven из реестра Gitea)
FROM 192.168.1.14:3000/deploy/base:maven-3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /build

# Копируем конфиг и качаем зависимости
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходники и собираем JAR
COPY src ./src
RUN mvn clean package -DskipTests -B

# Этап 2: Запуск (локальный образ JRE)
FROM 192.168.1.14:3000/deploy/base:eclipse-temurin-17-jre-alpine
WORKDIR /app

# Безопасность: работаем не от root
RUN addgroup -S hospital && adduser -S hospital -G hospital
USER hospital

# Копируем результат сборки из первого этапа
COPY --from=builder /build/target/*.jar app.jar

# Настройки для стабильной работы
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC"
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]