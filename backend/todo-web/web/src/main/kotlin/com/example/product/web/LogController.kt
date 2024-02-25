package com.example.product.web

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.time.Duration.Companion.seconds

@RestController
class LogController {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/log")
    suspend fun log(): String {
        logger.info { "Before delay" }
        delay(1.seconds)
        logger.info { "After delay" }
        return "Hello, World!"
    }
}
