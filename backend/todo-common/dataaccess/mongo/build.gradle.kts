plugins {
    id("kotlin-module-conventions")
    id("test-fixture-conventions")
}

dependencies {
    implementation(project(":backend:todo-common:domain"))
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    testFixturesApi(libs.kotest)
    testFixturesApi(libs.kotest.testcontainers)
    testFixturesApi("org.testcontainers:mongodb")
}
