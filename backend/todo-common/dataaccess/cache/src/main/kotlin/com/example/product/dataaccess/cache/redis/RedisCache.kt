package com.example.product.dataaccess.cache.redis

import com.example.product.domain.port.out.CachePort
import com.example.product.domain.port.out.TtlCalculator
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import kotlin.time.toJavaDuration

class RedisCache(
    override val enabled: Boolean,
    override val ttlCalculator: TtlCalculator,
    private val redisTemplate: ReactiveStringRedisTemplate,
    private val objectMapper: ObjectMapper,
) : CachePort() {
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
    ) {
        val valueAsString = objectMapper.writeValueAsString(value)
        val ttl = ttlCalculator.calculate(value).toJavaDuration()
        redisTemplate.opsForValue().set(key, valueAsString, ttl)
            .awaitSingleOrNull()
    }

    override suspend fun clear() {
        redisTemplate.connectionFactory.reactiveConnection.serverCommands().flushAll()
            .awaitSingleOrNull()
    }
}
