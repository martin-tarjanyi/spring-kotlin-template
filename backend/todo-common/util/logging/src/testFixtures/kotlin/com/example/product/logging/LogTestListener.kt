package com.example.product.logging

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import org.slf4j.Logger.ROOT_LOGGER_NAME
import org.slf4j.LoggerFactory
import java.util.concurrent.CopyOnWriteArrayList

class LogTestListener private constructor(private val logger: Logger) : TestListener {
    constructor(name: String = ROOT_LOGGER_NAME) : this(LoggerFactory.getLogger(name) as Logger)

    private val appender = InMemoryAppender()

    val events: List<ILoggingEvent>
        get() = appender.events

    override suspend fun beforeAny(testCase: TestCase) {
        appender.start()
        logger.addAppender(appender)
    }

    override suspend fun afterAny(
        testCase: TestCase,
        result: TestResult,
    ) {
        logger.detachAppender(appender)
        appender.stop()
        appender.clear()
    }
}

private class InMemoryAppender : AppenderBase<ILoggingEvent>() {
    val events = CopyOnWriteArrayList<ILoggingEvent>()

    override fun append(event: ILoggingEvent) {
        events.add(event)
    }

    fun clear() {
        events.clear()
    }
}
