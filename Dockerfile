FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN apt-get update && apt-get install -y maven && mvn -q -DskipTests package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN groupadd -r appgroup && useradd -r -g appgroup -u 1001 appuser
COPY --from=build /build/target/real-income-api-*.jar app.jar
RUN mkdir -p /app/data && chown -R appuser:appgroup /app
USER appuser
EXPOSE 8081
HEALTHCHECK --interval=30s --timeout=5s --start-period=15s \
  CMD curl -sf http://localhost:8081/api/rpi/latest-year || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
