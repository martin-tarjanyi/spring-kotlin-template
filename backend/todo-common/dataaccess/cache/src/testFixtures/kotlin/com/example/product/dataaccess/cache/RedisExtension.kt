package com.example.product.dataaccess.cache

import com.redis.testcontainers.RedisContainer
import io.kotest.core.listeners.AfterEachListener
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

object RedisExtension : BeforeProjectListener, AfterProjectListener, AfterEachListener {
    private val container = RedisContainer("redis:7.2.4").withReuse(true)

    override suspend fun beforeProject() {
        if (!container.isRunning) {
            container.start()
        }
    }

    override suspend fun afterEach(
        testCase: TestCase,
        result: TestResult,
    ) {
        container.execInContainer("redis-cli", "FLUSHALL")
    }

    fun host(): String = container.host

    fun port(): Int = container.redisPort
}
