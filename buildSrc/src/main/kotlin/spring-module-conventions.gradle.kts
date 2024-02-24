import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    kotlin("plugin.spring")
    id("kotlin-module-conventions")
}

// Workaround for using version catalogs in precompiled script plugins.
// See https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
val libs = the<LibrariesForLibs>()

dependencies {
    // prod
    implementation(project(":backend:todo-common:util:logging"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // test
    testImplementation(libs.kotest.spring)
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
