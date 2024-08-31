plugins {
    id("kotlin-module-conventions")
    id("test-fixture-conventions")
}

dependencies {
    implementation("ch.qos.logback:logback-classic")
    implementation("org.mongodb:bson")
    implementation(libs.logstash.encoder)
    implementation(libs.loki.logback)

    testFixturesImplementation("ch.qos.logback:logback-classic")
}
