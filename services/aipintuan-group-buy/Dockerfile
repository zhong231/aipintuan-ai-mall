FROM maven:3.9.9-eclipse-temurin-8 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY aipintuan-group-buy-api ./aipintuan-group-buy-api
COPY aipintuan-group-buy-app ./aipintuan-group-buy-app
COPY aipintuan-group-buy-domain ./aipintuan-group-buy-domain
COPY aipintuan-group-buy-trigger ./aipintuan-group-buy-trigger
COPY aipintuan-group-buy-infrastructure ./aipintuan-group-buy-infrastructure
COPY aipintuan-group-buy-types ./aipintuan-group-buy-types
RUN mvn -B -DskipTests package

FROM eclipse-temurin:8-jre
WORKDIR /app
COPY --from=build /workspace/aipintuan-group-buy-app/target/aipintuan-group-buy-app.jar app.jar
EXPOSE 8091
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=public"]
