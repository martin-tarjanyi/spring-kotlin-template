package com.example.product.web

import io.micrometer.tracing.Tracer
import org.springframework.stereotype.Component
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange

@Component
class TraceIdFilter(private val tracer: Tracer) : CoWebFilter() {
    override suspend fun filter(
        exchange: ServerWebExchange,
        chain: CoWebFilterChain,
    ) {
        tracer.currentSpan()?.context()?.traceId()?.also {
            exchange.response.headers["traceid"] = listOf(it)
        }
        chain.filter(exchange)
    }
}
