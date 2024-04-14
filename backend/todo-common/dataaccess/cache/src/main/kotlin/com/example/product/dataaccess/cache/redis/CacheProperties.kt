package com.example.product.dataaccess.cache.redis

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "caches")
data class CacheListProperties(
    val distributed: CacheProperties,
    val inMemory: CacheProperties,
)

data class CacheProperties(
    val enabled: Boolean = true,
    val minTtl: Duration = Duration.ofSeconds(10),
    val maxTtl: Duration = Duration.ofSeconds(20),
)

@ConfigurationProperties(prefix = "redis")
data class RedisListProperties(
    val common: RedisProperties,
)

data class RedisProperties(
    val host: String,
    val port: Int = 6379,
    val timeout: Duration = Duration.ofSeconds(2),
)
