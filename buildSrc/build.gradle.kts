plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // Workaround for using version catalogs in precompiled script plugins.
    // See https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
    implementation(libs.kotlin.allopen)
    implementation(libs.kotlin.gradle)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
