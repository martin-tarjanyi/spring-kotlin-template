# Spring Kotlin Template

[![Build](https://github.com/martin-tarjanyi/spring-kotlin-template/actions/workflows/build.yml/badge.svg)](https://github.com/martin-tarjanyi/spring-kotlin-template/actions/workflows/build.yml)

## Intro

Sample application written in Kotlin with Spring Boot following hexagonal architecture.

## Links

* [Swagger](http://localhost:8080/swagger-ui.html)
* [Grafana](http://localhost:3000)
* [RedisInsight](http://localhost:5540)

## Development

### Requirements

* Docker
* Java 21+
* IntelliJ IDEA

### Recommended

* IntelliJ plugins
    * Kotest
    * Ktlint
    * Gradle Version Catalogs

## Technologies

### Core

* Spring Boot 3+ (Webflux)
* Kotlin with coroutines
* Gradle 8+
    * Multi-module structure
    * Version catalog
    * Convention plugins (see [buildSrc](buildSrc/src/main/kotlin))
        * Reuse build logic in different modules
* Swagger
* Mongo
* Redis
* Spring HTTP interfaces
* Structured logging with JSON support
* Tracing with Micrometer
* Mapstruct - automatic mapping
* Dependency Management
    * Uses Spring managed dependency versions where possible
    * Other library versions are managed via [Gradle version catalog](gradle/libs.versions.toml)

### Testing

* Kotest
* Mockk
* Testcontainers
* Wiremock
