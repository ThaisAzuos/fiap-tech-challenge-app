# syntax=docker/dockerfile:1
FROM alpine
RUN echo "Hello"

# Estágio 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
VOLUME /tmp

# Copia o JAR (nome fixo para evitar problemas no CD)
COPY --from=build /app/target/meu-projeto-*.jar app.jar

EXPOSE 8080

# Cria grupo e usuário não-root
RUN addgroup -S javauser && adduser -S -G javauser javauser
USER javauser

# Healthcheck para Kubernetes
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Otimização JVM para container
ENV JAVA_OPTS="-XX:+UseContainerSupport -XshowSettings:vm -XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]