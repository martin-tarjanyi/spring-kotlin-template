package com.example.product.dataaccess.http

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.ShouldSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestPropertySource

@SpringBootTest(classes = [BaseHttpIntegrationTest.TestConfig::class])
@EnableAutoConfiguration
@TestPropertySource(
    properties = [
        "pokemon-api.http.readTimeout=1s",
        "star-wars-api.http.readTimeout=1s",
    ],
)
@ActiveProfiles("test")
abstract class BaseHttpIntegrationTest : ShouldSpec() {
    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("pokemon-api.http.baseUrl") {
                "http://localhost:${WiremockExtension.port()}/api/v2/"
            }
            registry.add("star-wars-api.http.baseUrl") {
                "http://localhost:${WiremockExtension.port()}"
            }
        }
    }

    @Configuration
    @ComponentScan
    internal class TestConfig
}
