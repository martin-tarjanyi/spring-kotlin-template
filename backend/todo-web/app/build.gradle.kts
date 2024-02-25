plugins {
    id("spring-module-conventions")
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.git.properties)
}

dependencies {
    implementation(project(":backend:todo-web:web"))
    implementation(project(":backend:todo-common:domain"))
    implementation(project(":backend:todo-common:dataaccess:mongo"))
    implementation(project(":backend:todo-common:dataaccess:http"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("io.micrometer:micrometer-tracing-bridge-otel")

    developmentOnly(platform(libs.spring.boot.dependencies))
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    integrationTestImplementation(testFixtures(project(":backend:todo-common:dataaccess:mongo")))
    integrationTestImplementation(testFixtures(project(":backend:todo-common:util:logging")))
}

springBoot {
    buildInfo {
        excludes = setOf("time")
    }
}

// app integration test should run after other integration tests as it has the biggest scope
tasks.named("integrationTest") {
    shouldRunAfter(
        rootProject.subprojects
            .filter { it != project }
            .flatMap { it.tasks }
            .filter { it.name == "integrationTest" },
    )
}
