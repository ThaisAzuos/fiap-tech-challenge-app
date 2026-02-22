# Estágio de Build: Usa uma imagem com Maven e JDK para compilar a aplicação
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia o pom.xml e baixa as dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o restante do código-fonte e compila a aplicação
COPY src ./src
RUN mvn clean install -DskipTests

# Estágio de Produção: Usa uma imagem leve apenas com o JRE
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copia o .jar compilado do estágio de build
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta que a aplicação usa
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
