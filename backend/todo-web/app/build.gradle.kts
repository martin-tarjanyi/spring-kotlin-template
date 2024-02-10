plugins {
	id("kotlin-module-conventions")
	alias(libs.plugins.spring.boot)
	alias(libs.plugins.git.properties)
}

dependencies {
	implementation(project(":backend:todo-common:domain"))
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("io.micrometer:micrometer-tracing-bridge-otel")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
}

springBoot {
	buildInfo {
		excludes.set(setOf("time"))
	}
}

// app integration test should run after other integration tests as it has the biggest scope
tasks.named("integrationTest") {
	shouldRunAfter(rootProject.subprojects
		.filter { it != project }
		.flatMap { it.tasks }
		.filter { it.name == "integrationTest" })
}
