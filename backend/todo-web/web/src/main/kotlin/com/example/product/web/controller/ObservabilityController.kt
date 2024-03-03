package com.example.product.web.controller

import com.example.product.domain.port.out.SpaceOperaPort
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.delay
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@RestController
@Tag(name = "Observability", description = "Observability testing endpoints")
class ObservabilityController(private val spaceOperaPort: SpaceOperaPort) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/log")
    @ApiResponse(content = [Content(schema = Schema(example = "Hello, World!"))])
    suspend fun log(): String {
        logger.info { "Before delay" }
        delay(1.seconds)
        logger.info { "After delay" }
        return "Hello, World!"
    }

    @GetMapping("/trace")
    suspend fun trace(): String {
        logger.info { "Before delay" }
        delay(100.milliseconds)
        logger.info { "After delay" }

        val character = spaceOperaPort.randomCharacter()
        logger.info { "After call" }

        return character.name
    }
}
