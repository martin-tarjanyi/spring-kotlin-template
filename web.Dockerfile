# syntax=docker/dockerfile:experimental
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY gradlew gradlew
COPY gradle gradle
COPY settings.gradle.kts settings.gradle.kts
COPY buildSrc buildSrc
COPY backend backend
COPY .git .git
RUN --mount=type=cache,target=/root/.gradle ./gradlew backend:todo-web:app:bootJar
RUN java -Djarmode=layertools -jar backend/todo-web/app/build/libs/app.jar extract --destination target/extracted

FROM eclipse-temurin:21-jdk-alpine
RUN addgroup -S prod && adduser -S prod -G prod
VOLUME /prod
USER prod
ENV SPRING_PROFILES_ACTIVE=prod
ARG EXTRACTED=/app/target/extracted
WORKDIR application
COPY --from=build ${EXTRACTED}/dependencies/ ./
COPY --from=build ${EXTRACTED}/spring-boot-loader/ ./
COPY --from=build ${EXTRACTED}/snapshot-dependencies/ ./
COPY --from=build ${EXTRACTED}/application/ ./
ENTRYPOINT ["java","org.springframework.boot.loader.launch.JarLauncher"]
