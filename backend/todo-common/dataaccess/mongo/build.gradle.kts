plugins {
    id("spring-module-conventions")
    id("test-fixture-conventions")
    id("mapstruct-conventions")
}

dependencies {
    implementation(project(":backend:todo-common:domain"))
    implementation(libs.mongo.coroutine)
    implementation(libs.mongo.bson.kotlinx)
    implementation("org.springframework:spring-context")

    testImplementation("org.springframework.boot:spring-boot-testcontainers")

    testFixturesApi(libs.kotest)
    testFixturesApi(libs.kotest.testcontainers)
    testFixturesApi("org.testcontainers:mongodb")
}
