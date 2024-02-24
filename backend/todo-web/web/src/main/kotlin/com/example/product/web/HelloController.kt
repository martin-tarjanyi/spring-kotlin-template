package com.example.product.web

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.time.Duration.Companion.seconds

@RestController
internal class HelloController {
    val logger = KotlinLogging.logger {}

    @GetMapping("/hello")
    suspend fun hello(): String {
        logger.info { "Before delay" }
        delay(1.seconds)
        logger.info { "After delay" }
        return "Hello, World!"
    }
}
