# Spring Kotlin Template

[![Build](https://github.com/martin-tarjanyi/spring-kotlin-template/actions/workflows/gradle.yml/badge.svg)](https://github.com/martin-tarjanyi/spring-kotlin-template/actions/workflows/gradle.yml)

## Intro

Sample application written in Kotlin with Spring Boot following hexagonal architecture.

## Links
* [Swagger](http://localhost:8080/swagger-ui.html)

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
  * Version catalog
  * Convention plugins
* Swagger
* Mongo
* Spring HTTP interfaces
* Structure logging with JSON support
* Tracing with Micrometer
* Dependency Management
  * Uses Spring managed dependency versions where possible
  * Other library versions are managed via [Gradle version catalog](gradle/libs.versions.toml)

### Testing

* Kotest
* Mockk
* Testcontainers
* Wiremock
