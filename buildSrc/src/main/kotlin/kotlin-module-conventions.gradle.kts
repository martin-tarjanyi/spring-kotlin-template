import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint")
}

group = "com.example.product"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

// Workaround for using version catalogs in precompiled script plugins.
// See https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
val libs = the<LibrariesForLibs>()

dependencies {
    // prod
    implementation(platform(libs.spring.boot.dependencies))
    implementation(platform(libs.spring.cloud.dependencies))
    implementation(libs.kotlin.logging)
    annotationProcessor(platform(libs.spring.boot.dependencies))

    // test
    testImplementation(libs.kotest)
    testImplementation(libs.mockk)
}

testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                targets {
                    all {
                        testTask.configure {
                            jvmArgs("-XX:+EnableDynamicAgentLoading")
                            testLogging {
                                events =
                                    setOf(
                                        TestLogEvent.PASSED,
                                        TestLogEvent.SKIPPED,
                                        TestLogEvent.FAILED,
                                        TestLogEvent.STANDARD_OUT,
                                        TestLogEvent.STANDARD_ERROR,
                                    )
                                exceptionFormat = TestExceptionFormat.FULL
                            }
                        }
                    }
                }
            }
        }

        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }

        val integrationTest by registering(JvmTestSuite::class) {
            dependencies {
                implementation(project())
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }
    }
}

tasks.named("check") {
    dependsOn(testing.suites.named("integrationTest"))
}

// ensures integration test can use unit test dependencies
val integrationTestImplementation by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}
