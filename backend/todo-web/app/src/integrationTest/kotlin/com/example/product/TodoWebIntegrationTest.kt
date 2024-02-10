package com.example.product

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.test.TestCase
import io.kotest.extensions.spring.SpringExtension
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TodoWebIntegrationTest(private val webTestClient: WebTestClient) : ShouldSpec() {
    override fun extensions() = listOf(SpringExtension)

    override suspend fun beforeAny(testCase: TestCase) {
        super.beforeAny(testCase)
    }

    init {
        should("return hello") {
            // call hello endpoint and verify result
            webTestClient.get().uri("/hello")
                .exchange()
                .expectStatus().isOk
                .expectBody<String>().isEqualTo("Hello, World!")
        }
    }
}
