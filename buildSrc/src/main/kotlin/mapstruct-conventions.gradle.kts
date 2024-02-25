import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    kotlin("jvm")
    kotlin("kapt")
}

val libs = the<LibrariesForLibs>()

dependencies {
    implementation(libs.mapstruct)
    kapt(libs.mapstruct.processor)
}

kapt {
    arguments {
        // https://kotlinlang.org/docs/reference/kapt.html#annotation-processor-arguments
        // Set Mapstruct Configuration options here
        // https://mapstruct.org/documentation/stable/reference/html/#configuration-options
        arg("mapstruct.unmappedTargetPolicy", "ERROR")
    }
}
