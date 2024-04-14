package com.example.product.domain.port.out

import com.fasterxml.jackson.core.type.TypeReference
import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TestCache(
    override val enabled: Boolean = true,
    override val ttlCalculator: TtlCalculator = TtlCalculator(1.seconds, 2.seconds),
    private val failOnGet: Boolean = false,
    private val failOnSet: Boolean = false,
    private val delayOnSet: Duration = 0.seconds,
) : CachePort() {
    private val cache = ConcurrentHashMap<String, String>()

    override suspend fun <T> internalGet(
        key: String,
        typeRef: TypeReference<T>,
    ): T? {
        if (failOnGet) {
            throw RuntimeException("Test error on key read")
        }
        return cache[key] as T?
    }

    override suspend fun <T : Any> internalSet(
        key: String,
        value: T,
    ) {
        if (failOnSet) {
            throw RuntimeException("Test error on key write")
        }
        if (delayOnSet > 0.seconds) {
            delay(delayOnSet)
        }
        cache[key] = value.toString()
    }

    override suspend fun clear() {
        cache.clear()
    }
}
