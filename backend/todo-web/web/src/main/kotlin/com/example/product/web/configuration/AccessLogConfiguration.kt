package com.example.product.web.configuration

import io.github.oshai.kotlinlogging.KotlinLogging
import org.slf4j.MDC
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.netty.http.server.logging.AccessLog
import reactor.netty.http.server.logging.AccessLogArgProvider

@Configuration
internal class AccessLogConfiguration {
    private val logger = KotlinLogging.logger("ACCESS_LOG")
    private val excludedPathPrefixes =
        setOf("/actuator", "/v3/api-docs", "/swagger-ui", "/swagger-resources", "/webjars")

    @Bean
    fun accessLogCustomizer() =
        NettyServerCustomizer { server ->
            server.accessLog(true) { args -> accessLogFactory(args) }
        }

    private fun accessLogFactory(args: AccessLogArgProvider): AccessLog? {
        if (excludedPathPrefixes.any { args.uri()?.startsWith(it) == true }) {
            return null
        }

        MDC.putCloseable("traceId", args.responseHeader("traceid")?.toString()).use {
            logger.atInfo {
                message = "HTTP Server Access"
                payload = listOfNotNull(
                    args.method()?.let { "method" to it },
                    args.uri()?.let { "uri" to it },
                    args.status()?.let { "status" to it },
                    "durationMs" to args.duration(),
                    "contentLength" to args.contentLength(),
                    args.connectionInformation()?.remoteAddress()?.let { "remoteAddress" to it.toString() },
                ).toMap()
            }
        }
        // disable built-in logging by returning null, so we can use our custom logging
        return null
    }
}
