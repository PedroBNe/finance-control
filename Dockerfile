# Estágio 1: Build
# Usamos a imagem do JDK 25 diretamente
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

# Instala o Maven manualmente (o Alpine facilita muito isso)
RUN apk add --no-cache maven

# Copia apenas o pom.xml para cache de dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código e gera o jar
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Runtime
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Segurança: usuário não-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/finance-control-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
