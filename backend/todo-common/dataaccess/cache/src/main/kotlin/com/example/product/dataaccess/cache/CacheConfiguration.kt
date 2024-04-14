package com.example.product.dataaccess.cache

import com.example.product.dataaccess.cache.caffeine.CaffeineCache
import com.example.product.dataaccess.cache.redis.CacheListProperties
import com.example.product.dataaccess.cache.redis.CacheProperties
import com.example.product.dataaccess.cache.redis.RedisCache
import com.example.product.dataaccess.cache.redis.RedisListProperties
import com.example.product.dataaccess.cache.redis.RedisProperties
import com.example.product.domain.port.out.CachePort
import com.example.product.domain.port.out.TtlCalculator
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import kotlin.time.toKotlinDuration

@Configuration
@EnableConfigurationProperties(CacheListProperties::class, RedisListProperties::class)
class CacheConfiguration(
    private val redisListProperties: RedisListProperties,
    private val cacheListProperties: CacheListProperties,
    private val objectMapper: ObjectMapper,
) {
    @Primary
    @Bean("distributed")
    fun distributedCache(): CachePort {
        return createRedisCache(commonRedis(), cacheListProperties.distributed)
    }

    @Bean("inMemory")
    fun inMemoryCache(): CachePort {
        return createCaffeineCache(cacheListProperties.inMemory)
    }

    @Bean("common")
    fun commonRedis(): ReactiveStringRedisTemplate {
        return createRedisTemplate(redisListProperties.common)
    }

    private fun createRedisCache(
        template: ReactiveStringRedisTemplate,
        properties: CacheProperties,
    ): RedisCache {
        val ttlCalculator = TtlCalculator(properties.minTtl.toKotlinDuration(), properties.maxTtl.toKotlinDuration())
        return RedisCache(properties.enabled, ttlCalculator, template, objectMapper)
    }

    private fun createRedisTemplate(properties: RedisProperties): ReactiveStringRedisTemplate {
        val redisConfiguration = RedisStandaloneConfiguration(properties.host, properties.port)
        val clientConfiguration = LettuceClientConfiguration.builder()
            .commandTimeout(properties.timeout)
            .build()
        val connectionFactory = LettuceConnectionFactory(redisConfiguration, clientConfiguration)
            .apply { afterPropertiesSet() }
        return ReactiveStringRedisTemplate(connectionFactory)
    }

    private fun createCaffeineCache(properties: CacheProperties): CaffeineCache {
        return CaffeineCache(
            properties.enabled,
            TtlCalculator(properties.minTtl.toKotlinDuration(), properties.maxTtl.toKotlinDuration()),
        )
    }
}
