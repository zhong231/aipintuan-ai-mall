FROM maven:3.9.9-eclipse-temurin-8 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY s-pay-mall-ddd-api ./s-pay-mall-ddd-api
COPY s-pay-mall-ddd-app ./s-pay-mall-ddd-app
COPY s-pay-mall-ddd-domain ./s-pay-mall-ddd-domain
COPY s-pay-mall-ddd-trigger ./s-pay-mall-ddd-trigger
COPY s-pay-mall-ddd-infrastructure ./s-pay-mall-ddd-infrastructure
COPY s-pay-mall-ddd-types ./s-pay-mall-ddd-types
RUN mvn -B -DskipTests package

FROM eclipse-temurin:8-jre
WORKDIR /app
COPY --from=build /workspace/s-pay-mall-ddd-app/target/s-pay-mall-ddd-app.jar app.jar
EXPOSE 8070
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=public"]

