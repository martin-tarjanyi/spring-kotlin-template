package com.example.product

import com.example.product.dataaccess.mongo.MongoExtension
import com.example.product.web.configuration.ApiDocsProperties
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.test.TestCase
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureObservability(tracing = true, metrics = false)
@AutoConfigureWebTestClient(timeout = "120s")
@ActiveProfiles("test")
@TestPropertySource(properties = ["management.otlp.tracing.endpoint=false", "springdoc.cache.disabled=true"])
abstract class BaseWebIntegrationTest : ShouldSpec() {
    @Autowired
    protected lateinit var webTestClient: WebTestClient

    @MockBean
    protected lateinit var apiDocsProperties: ApiDocsProperties

    override suspend fun beforeEach(testCase: TestCase) {
        super.beforeEach(testCase)
        given(apiDocsProperties.publishNonCustomerFacingEndpoints).willReturn(true)
        given(apiDocsProperties.publishInDevelopmentProperties).willReturn(true)
    }

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
