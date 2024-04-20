package com.example.product.domain.port.out

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

val logger = KotlinLogging.logger {}

abstract class CachePort(
    private val enabled: Boolean,
    private val defaultTtlProvider: TtlProvider,
) {
    suspend fun <T> get(
        key: String,
        typeRef: TypeReference<T>,
    ): T? {
        return if (enabled) {
            runCatching { internalGet(key, typeRef) }
                .onSuccess {
                    if (it != null) {
                        logger.debug { "Cache hit for key: $key" }
                    } else {
                        logger.debug { "Cache miss for key: $key" }
                    }
                }
                .onFailure { e ->
                    logger.error(e) { "Failed to read key \"$key\" from cache" }
                }
                .getOrNull()
        } else {
            null
        }
    }

    protected abstract suspend fun <T> internalGet(
        key: String,
        typeRef: TypeReference<T>,
    ): T?

    suspend fun <T : Any> set(
        key: String,
        value: T,
        ttlProvider: TtlProvider? = null,
    ) {
        if (enabled) {
            runCatching {
                val ttl = (value as? ExpiringCacheValue)?.ttl()
                    ?: ttlProvider?.ttl()
                    ?: defaultTtlProvider.ttl()
                internalSet(key, value, ttl)
            }.onFailure { e ->
                logger.error(e) { "Failed to set value for key \"$key\" in cache" }
            }
        }
    }

    protected abstract suspend fun <T : Any> internalSet(
        key: String,
        value: T,
        ttl: Duration,
    )

    abstract suspend fun clear()
}

suspend inline fun <reified T : Any?> CachePort.cache(
    key: String,
    ttlProvider: TtlProvider? = null,
    valueSupplier: () -> T,
): T {
    return get(key, jacksonTypeRef<T>())
        ?: valueSupplier().also { value ->
            value?.let {
                GlobalScope.launch(Dispatchers.Default) {
                    set(key, it, ttlProvider)
                }
            }
        }
}

interface TtlProvider {
    fun ttl(): Duration
}

class RandomTtlProvider(private val min: Duration, private val max: Duration) : TtlProvider {
    override fun ttl() =
        (min.inWholeMilliseconds..max.inWholeMilliseconds)
            .random().milliseconds
}

interface ExpiringCacheValue {
    fun ttl(): Duration
}
