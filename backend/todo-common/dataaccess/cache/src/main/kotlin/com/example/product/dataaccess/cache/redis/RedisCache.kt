package com.example.product.dataaccess.cache.redis

import com.example.product.domain.port.out.CachePort
import com.example.product.domain.port.out.TtlProvider
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class RedisCache(
    enabled: Boolean,
    defaultTtlProvider: TtlProvider,
    private val redisTemplate: ReactiveStringRedisTemplate,
    private val objectMapper: ObjectMapper,
) : CachePort(enabled, defaultTtlProvider) {
    override suspend fun <T> internalGet(
        key: String,
        typeRef: TypeReference<T>,
    ): T? {
        return redisTemplate.opsForValue().get(key)
            .awaitSingleOrNull()
            ?.let {
                objectMapper.readValue(it, typeRef)
            }
    }

    override suspend fun <T : Any> internalSet(
        key: String,
        value: T,
        ttl: Duration,
    ) {
        val valueAsString = objectMapper.writeValueAsString(value)
        redisTemplate.opsForValue().set(key, valueAsString, ttl.toJavaDuration())
            .awaitSingleOrNull()
    }

    override suspend fun clear() {
        redisTemplate.connectionFactory.reactiveConnection.serverCommands().flushAll()
            .awaitSingleOrNull()
    }
}
