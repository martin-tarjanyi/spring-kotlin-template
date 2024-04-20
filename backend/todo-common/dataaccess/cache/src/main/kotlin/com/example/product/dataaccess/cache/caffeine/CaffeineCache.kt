package com.example.product.dataaccess.cache.caffeine

import com.example.product.domain.port.out.CachePort
import com.example.product.domain.port.out.TtlProvider
import com.fasterxml.jackson.core.type.TypeReference
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Expiry
import kotlin.time.Duration

class CaffeineCache(enabled: Boolean, defaultTtlProvider: TtlProvider) : CachePort(enabled, defaultTtlProvider) {
    private val caffeine = Caffeine.newBuilder()
        .expireAfter(object : Expiry<String, ValueWrapper<*>> {
            override fun expireAfterCreate(
                key: String,
                value: ValueWrapper<*>,
                currentTime: Long,
            ): Long {
                return value.ttl.inWholeNanoseconds
            }

            override fun expireAfterUpdate(
                key: String?,
                value: ValueWrapper<*>?,
                currentTime: Long,
                currentDuration: Long,
            ): Long = currentDuration

            override fun expireAfterRead(
                key: String?,
                value: ValueWrapper<*>,
                currentTime: Long,
                currentDuration: Long,
            ): Long = currentDuration
        })
        .build<String, ValueWrapper<*>>()

    override suspend fun <T> internalGet(
        key: String,
        typeRef: TypeReference<T>,
    ): T? {
        return caffeine.getIfPresent(key)?.value as T?
    }

    override suspend fun <T : Any> internalSet(
        key: String,
        value: T,
        ttl: Duration,
    ) {
        caffeine.put(key, ValueWrapper(value, ttl))
    }

    override suspend fun clear() {
        caffeine.invalidateAll()
    }
}

private data class ValueWrapper<T>(
    val value: T,
    val ttl: Duration,
)
