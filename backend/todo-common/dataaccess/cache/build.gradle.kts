plugins {
    id("spring-module-conventions")
    id("test-fixture-conventions")
}

dependencies {
    implementation(project(":backend:todo-common:domain"))
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("org.springframework:spring-context")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    testImplementation("org.springframework.boot:spring-boot-testcontainers")

    testFixturesApi(libs.kotest)
    testFixturesApi(libs.kotest.testcontainers)
    testFixturesApi(libs.testcontainers.redis)
}
