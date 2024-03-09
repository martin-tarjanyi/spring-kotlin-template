plugins {
    id("kotlin-module-conventions")
    id("test-fixture-conventions")
}

dependencies {
    api(libs.kotest)
    implementation(libs.kotlin.logging)
}
