plugins {
    id("spring-module-conventions")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation(libs.springdoc.ui)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j")
    implementation("io.micrometer:micrometer-tracing")
}
