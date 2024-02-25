package com.example.product

import com.example.product.logging.LogTestListener
import io.kotest.inspectors.shouldForAny
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotBeBlank
import org.springframework.test.web.reactive.server.expectBody

class LogWebIntegrationTest : BaseWebIntegrationTest() {
    init {
        val logListener = LogTestListener()
        listener(logListener)

        context("log") {
            should("return in header and log trace ID") {
                val result = webTestClient.get().uri("/log")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody<String>().isEqualTo("Hello, World!")
                    .returnResult()

                val traceId = result.responseHeaders["traceid"]?.firstOrNull().shouldNotBeBlank()!!

                logListener.events
                    .shouldForAny { event ->
                        event.formattedMessage.shouldContain("Before delay")
                        event.mdcPropertyMap["traceId"].shouldContain(traceId)
                    }
                    .shouldForAny { event ->
                        event.formattedMessage.shouldContain("After delay")
                        event.mdcPropertyMap["traceId"].shouldContain(traceId)
                    }
            }
        }
    }
}
