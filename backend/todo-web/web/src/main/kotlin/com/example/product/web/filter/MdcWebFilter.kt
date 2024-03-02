package com.example.product.web.filter

import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange

/**
 * This might not be needed once https://github.com/micrometer-metrics/tracing/issues/174 is resolved.
 */
@Component
internal class MdcWebFilter : CoWebFilter() {
    override suspend fun filter(
        exchange: ServerWebExchange,
        chain: CoWebFilterChain,
    ): Unit =
        withContext(MDCContext()) {
            chain.filter(exchange)
        }
}
