# syntax=docker/dockerfile:experimental
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY backend/todo-web/app/build/libs/app.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract --destination target/extracted

FROM eclipse-temurin:21-jdk-alpine
RUN addgroup -S prod && adduser -S prod -G prod
VOLUME /tmp
USER prod
ARG EXTRACTED=/app/target/extracted
WORKDIR application
COPY --from=build ${EXTRACTED}/dependencies/ ./
COPY --from=build ${EXTRACTED}/spring-boot-loader/ ./
COPY --from=build ${EXTRACTED}/snapshot-dependencies/ ./
COPY --from=build ${EXTRACTED}/application/ ./
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
