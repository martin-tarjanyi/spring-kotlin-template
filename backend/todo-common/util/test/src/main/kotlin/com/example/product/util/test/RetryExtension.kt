package com.example.product.util.test

import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

class RetryExtension(private val maxAttempts: Int) : TestCaseExtension {
    private val logger = KotlinLogging.logger {}

    override suspend fun intercept(
        testCase: TestCase,
        execute: suspend (TestCase) -> TestResult,
    ): TestResult {
        var result = execute(testCase)
        var attempts = 1
        while (result.isErrorOrFailure && attempts++ < maxAttempts) {
            logger.warn {
                """Test case "${testCase.name.testName}" failed with "${result.errorOrNull?.toString()}", attempting retry..."""
            }
            result = execute(testCase)
        }
        return result
    }
}
