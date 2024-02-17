package com.example.product

import org.springframework.test.web.reactive.server.expectBody

class TodoWebIntegrationTest : BaseWebIntegrationTest() {
    init {
        context("hello") {
            should("return hello") {
                // call hello endpoint and verify result
                webTestClient.get().uri("/hello")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody<String>().isEqualTo("Hello, World!")
            }
        }
    }
}
