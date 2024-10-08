package com.example.product

import com.example.product.dataaccess.mongo.MongoExtension
import io.kotest.core.spec.style.ShouldSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureObservability(tracing = true, metrics = false)
@AutoConfigureWebTestClient(timeout = "120s")
@ActiveProfiles("test")
@TestPropertySource(properties = ["management.otlp.tracing.endpoint=false"])
abstract class BaseWebIntegrationTest : ShouldSpec() {
    @Autowired
    protected lateinit var webTestClient: WebTestClient

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("MONGO_CONNECTION_URI") {
                MongoExtension.connectionString()
            }
        }
    }
}
