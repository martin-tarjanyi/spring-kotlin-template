package com.example.product.dataaccess.mongo

import io.kotest.core.spec.style.ShouldSpec
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

@SpringBootTest(classes = [BaseMongoIntegrationTest.TestConfig::class])
@EnableAutoConfiguration
@ActiveProfiles("test")
internal abstract class BaseMongoIntegrationTest : ShouldSpec() {
    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("MONGO_CONNECTION_URI") {
                MongoExtension.connectionString()
            }
        }
    }

    @Configuration
    @ComponentScan
    internal class TestConfig
}
