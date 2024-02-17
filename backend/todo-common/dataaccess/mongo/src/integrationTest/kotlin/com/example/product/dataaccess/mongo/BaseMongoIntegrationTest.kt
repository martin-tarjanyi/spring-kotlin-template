package com.example.product.dataaccess.mongo

import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

@SpringBootTest(classes = [BaseMongoIntegrationTest.TestConfig::class])
@EnableAutoConfiguration
internal abstract class BaseMongoIntegrationTest : ShouldSpec() {
    override fun extensions(): List<Extension> = listOf(SpringExtension)

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
