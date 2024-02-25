plugins {
    id("spring-module-conventions")
    id("test-fixture-conventions")
}

dependencies {
    implementation(project(":backend:todo-common:domain"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("jakarta.validation:jakarta.validation-api")
    implementation("org.hibernate.validator:hibernate-validator")

    testFixturesApi(libs.wiremock)
}
