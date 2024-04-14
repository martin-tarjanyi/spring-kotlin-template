package com.example.product.dataaccess.cache.caffeine

import com.example.product.domain.port.out.CachePort
import com.example.product.domain.port.out.TtlCalculator
import com.fasterxml.jackson.core.type.TypeReference
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Expiry

class CaffeineCache(
    override val enabled: Boolean,
    override val ttlCalculator: TtlCalculator,
) : CachePort() {
    private val caffeine = Caffeine.newBuilder()
        .expireAfter(object : Expiry<String, Any> {
            override fun expireAfterCreate(
                key: String,
                value: Any,
                currentTime: Long,
            ): Long {
                return ttlCalculator.calculate(value).inWholeNanoseconds
            }

            override fun expireAfterUpdate(
                key: String?,
                value: Any?,
                currentTime: Long,
                currentDuration: Long,
            ): Long = currentDuration

            override fun expireAfterRead(
                key: String?,
                value: Any?,
                currentTime: Long,
                currentDuration: Long,
            ): Long = currentDuration
        })
        .build<String, Any>()

    override suspend fun <T> internalGet(
        key: String,
        typeRef: TypeReference<T>,
    ): T? {
        return caffeine.getIfPresent(key) as T?
    }

    override suspend fun <T : Any> internalSet(
        key: String,
        value: T,
    ) {
        caffeine.put(key, value)
    }

    override suspend fun clear() {
        caffeine.invalidateAll()
    }
}
