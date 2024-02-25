import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    `java-library`
    id("java-test-fixtures")
}

// Workaround for using version catalogs in precompiled script plugins.
// See https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
val libs = the<LibrariesForLibs>()

dependencies {
    testFixturesImplementation(platform(libs.spring.boot.dependencies))
    testFixturesImplementation(platform(libs.spring.cloud.dependencies))
    testFixturesImplementation(libs.kotest)
    testFixturesAnnotationProcessor(platform(libs.spring.boot.dependencies))
}
