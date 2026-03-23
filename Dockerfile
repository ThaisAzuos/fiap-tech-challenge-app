# Estágio de Build: Usa uma imagem com Maven e JDK para compilar a aplicação
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia o pom.xml e baixa as dependências
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline

# Copia o restante do código-fonte e compila a aplicação
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Estágio de Produção: Usa uma imagem leve apenas com o JRE
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copia o .jar compilado do estágio de build
COPY --from=build /app/target/*.jar app.jar

# Executa sem privilégios de root
RUN useradd --system --uid 1001 spring
USER 1001

# Expõe a porta que a aplicação usa
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
