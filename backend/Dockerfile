FROM gradle:8.13-jdk21 AS build
WORKDIR /workspace
COPY . .
RUN gradle --no-daemon clean installDist

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/build/install/elan-api/ ./
EXPOSE 8080
USER 10001
ENTRYPOINT ["./bin/elan-api"]
