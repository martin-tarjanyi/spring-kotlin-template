package com.example.product.dataaccess.cache

import com.example.product.dataaccess.cache.caffeine.CaffeineCache
import com.example.product.dataaccess.cache.redis.CacheListProperties
import com.example.product.dataaccess.cache.redis.CacheProperties
import com.example.product.dataaccess.cache.redis.RedisCache
import com.example.product.domain.port.out.CachePort
import com.example.product.domain.port.out.RandomTtlProvider
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import kotlin.time.toKotlinDuration

@Configuration
@EnableConfigurationProperties(CacheListProperties::class)
class CacheConfiguration(
    private val cacheListProperties: CacheListProperties,
    private val objectMapper: ObjectMapper,
) {
    @Primary
    @Bean("distributed")
    fun distributedCache(
        @Qualifier("common") redisTemplate: ReactiveStringRedisTemplate,
    ): CachePort {
        return createRedisCache(redisTemplate, cacheListProperties.distributed)
    }

    @Bean("inMemory")
    fun inMemoryCache(): CachePort {
        return createCaffeineCache(cacheListProperties.inMemory)
    }

    private fun createRedisCache(
        template: ReactiveStringRedisTemplate,
        properties: CacheProperties,
    ): RedisCache {
        val ttlProvider = RandomTtlProvider(properties.minTtl.toKotlinDuration(), properties.maxTtl.toKotlinDuration())
        return RedisCache(properties.enabled, ttlProvider, template, objectMapper)
    }

    private fun createCaffeineCache(properties: CacheProperties): CaffeineCache {
        return CaffeineCache(
            properties.enabled,
            RandomTtlProvider(properties.minTtl.toKotlinDuration(), properties.maxTtl.toKotlinDuration()),
        )
    }
}
