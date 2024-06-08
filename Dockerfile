# Use um imagem base com JDK 17
FROM eclipse-temurin:17-jdk-alpine as build

# Defina o diretório de trabalho
WORKDIR /app

# Copie o script do Gradle wrapper e os arquivos de configuração
COPY gradlew .
COPY gradle/wrapper/gradle-wrapper.jar gradle/wrapper/gradle-wrapper.properties gradle/wrapper/

# Certifique-se de que o script do Gradle wrapper seja executável
RUN chmod +x gradlew

# Copie os arquivos de construção do Gradle
COPY build.gradle settings.gradle ./

# Baixe as dependências do Gradle (incluindo o wrapper) e evite executar testes
RUN ./gradlew dependencies --no-daemon

# Copie o código-fonte
COPY src/ src/

# Empacote a aplicação em um jar
RUN ./gradlew bootJar --no-daemon

# Use uma imagem mais leve para o runtime
FROM eclipse-temurin:17-jre-alpine

# Defina o diretório de trabalho
WORKDIR /app

# Copie o arquivo jar do estágio de construção
COPY --from=build /app/build/libs/*.jar app.jar

# Exponha a porta na qual a aplicação será executada
EXPOSE 8080

# Execute a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
