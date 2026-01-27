# Estágio 1: Build (Compilação)
# Usamos uma imagem do Maven com Java 21 para gerar o JAR
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copia apenas o pom.xml primeiro para baixar as dependências (otimiza o cache do Docker)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código fonte e compila
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Runtime (Execução)
# Usamos uma imagem leve (JRE) para rodar a aplicação
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copia o JAR gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Define a porta da aplicação
EXPOSE 8080

# Comando para iniciar
ENTRYPOINT ["java", "-jar", "app.jar"]