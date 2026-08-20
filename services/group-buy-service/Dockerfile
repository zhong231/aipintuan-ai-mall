FROM maven:3.9.9-eclipse-temurin-8 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY group-buy-market-api ./group-buy-market-api
COPY group-buy-market-app ./group-buy-market-app
COPY group-buy-market-domain ./group-buy-market-domain
COPY group-buy-market-trigger ./group-buy-market-trigger
COPY group-buy-market-infrastructure ./group-buy-market-infrastructure
COPY group-buy-market-types ./group-buy-market-types
RUN mvn -B -DskipTests package

FROM eclipse-temurin:8-jre
WORKDIR /app
COPY --from=build /workspace/group-buy-market-app/target/group-buy-market-app.jar app.jar
EXPOSE 8091
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=public"]

