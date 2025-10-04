plugins {
    id("spring-module-conventions")
    id("test-fixture-conventions")
    alias(libs.plugins.netflix.dgs.codegen)
}

dependencies {
    implementation(project(":backend:todo-common:domain"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("jakarta.validation:jakarta.validation-api")
    implementation("org.hibernate.validator:hibernate-validator")
    implementation(libs.netflix.dgs.starter)

    testFixturesApi(libs.wiremock)
}

tasks.generateJava {
    schemaPaths.add("$projectDir/src/main/resources/graphql-client")
    packageName = "com.example.rickmorty.generated"
    generateClient = true
    kotlinAllFieldsOptional = true
}
