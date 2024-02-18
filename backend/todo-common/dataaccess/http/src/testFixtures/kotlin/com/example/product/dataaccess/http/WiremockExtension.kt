package com.example.product.dataaccess.http

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.AfterEachListener
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

object WiremockExtension : BeforeProjectListener, AfterProjectListener, AfterEachListener {
    private val logger = KotlinLogging.logger {}
    val wiremock = WireMockServer(wireMockConfig().dynamicPort())

    override suspend fun beforeProject() {
        super.beforeProject()
        if (!wiremock.isRunning) {
            wiremock.start()
            logger.info { "Started wiremock on port ${wiremock.port()}" }
        }
    }

    override suspend fun afterProject() {
        if (wiremock.isRunning) {
            wiremock.stop()
        }
        super.afterProject()
    }

    override suspend fun afterEach(
        testCase: TestCase,
        result: TestResult,
    ) {
        wiremock.resetMappings()
        super.afterEach(testCase, result)
    }

    fun port(): Int = wiremock.port()
}

object KotestProjectConfig : AbstractProjectConfig() {
    override fun extensions() = listOf(WiremockExtension)
}
