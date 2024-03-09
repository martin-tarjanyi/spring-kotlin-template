package com.example.product

import com.example.product.logging.LogTestListener
import com.example.product.util.test.RetryExtension
import io.kotest.core.test.TestCase
import io.kotest.inspectors.shouldForOne
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotBeBlank
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.toEntity

class ObservabilityWebIntegrationTest : BaseWebIntegrationTest() {
    @LocalServerPort
    private var port: Int = 0

    override suspend fun beforeTest(testCase: TestCase) {
        println("beforeTest")
    }

    init {
        val logListener = LogTestListener()
        register(logListener, RetryExtension(maxAttempts = 20))

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

            should("access log") {
                // Access log is not logged for some reason with WebTestClient, so we use a real one here
                val result = WebClient.create("http://localhost:$port").get().uri("/log")
                    .retrieve().toEntity<String>().awaitSingle()

                val traceId = result.headers["traceid"]?.firstOrNull().shouldNotBeBlank()!!

                logListener.events
                    .shouldForOne { event ->
                        event.loggerName shouldBe "ACCESS_LOG"
                        event.mdcPropertyMap["traceId"].shouldContain(traceId)
                    }
            }
        }

        context("test") {
            should("be intermittent but retried") {
                println("intermittent")
                (1..3).random() shouldBe 1
            }
        }
    }
}
