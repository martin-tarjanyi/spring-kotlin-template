plugins {
    id("kotlin-module-conventions")
    id("test-fixture-conventions")
}

dependencies {
    implementation("ch.qos.logback:logback-classic")
    implementation(libs.logstash.encoder)
}
