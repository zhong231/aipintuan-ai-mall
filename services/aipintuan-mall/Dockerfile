FROM maven:3.9.9-eclipse-temurin-8 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY aipintuan-mall-api ./aipintuan-mall-api
COPY aipintuan-mall-app ./aipintuan-mall-app
COPY aipintuan-mall-domain ./aipintuan-mall-domain
COPY aipintuan-mall-trigger ./aipintuan-mall-trigger
COPY aipintuan-mall-infrastructure ./aipintuan-mall-infrastructure
COPY aipintuan-mall-types ./aipintuan-mall-types
RUN mvn -B -DskipTests package

FROM eclipse-temurin:8-jre
WORKDIR /app
COPY --from=build /workspace/aipintuan-mall-app/target/aipintuan-mall-app.jar app.jar
EXPOSE 8070
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=public"]
