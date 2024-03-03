package com.example.product

import com.example.product.logging.LogTestListener
import io.kotest.inspectors.shouldForOne
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotBeBlank
import org.springframework.test.web.reactive.server.expectBody

class ObservabilityWebIntegrationTest : BaseWebIntegrationTest() {
    init {
        val logListener = LogTestListener()
        listener(logListener)

        context("log endpoint") {
            should("contain trace ID in header and log") {
                val result = webTestClient.get().uri("/log")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody<String>().isEqualTo("Hello, World!")
                    .returnResult()

                val traceId = result.responseHeaders["traceid"]?.firstOrNull().shouldNotBeBlank()!!

                logListener.events
                    .shouldForOne { event ->
                        event.formattedMessage.shouldContain("Before delay")
                        event.mdcPropertyMap["traceId"].shouldContain(traceId)
                    }
                    .shouldForOne { event ->
                        event.formattedMessage.shouldContain("After delay")
                        event.mdcPropertyMap["traceId"].shouldContain(traceId)
                    }
            }
        }
    }
}
