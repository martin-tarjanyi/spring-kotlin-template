package com.example.product.dataaccess.cache

import com.example.product.dataaccess.cache.redis.RedisListProperties
import com.example.product.dataaccess.cache.redis.RedisProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveStringRedisTemplate

@Configuration
@EnableConfigurationProperties(RedisListProperties::class)
class RedisConfiguration(
    private val redisListProperties: RedisListProperties,
) {
    @Bean("common")
    fun commonRedis(): ReactiveStringRedisTemplate {
        return createRedisTemplate(redisListProperties.common)
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
}
