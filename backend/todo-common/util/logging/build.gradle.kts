plugins {
    id("kotlin-module-conventions")
}

dependencies {
    implementation("ch.qos.logback:logback-classic")
    implementation(libs.logstash.encoder)
}
