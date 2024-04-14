package com.example.product.dataaccess.cache

import com.example.product.dataaccess.cache.redis.RedisCache
import com.example.product.domain.port.out.CachePort
import com.example.product.domain.port.out.TtlCalculator
import com.example.product.domain.port.out.cache
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@SpringBootTest
@ActiveProfiles("test")
class RedisCacheIntegrationTest : ShouldSpec() {
    @Autowired
    @Qualifier("testRedis")
    private lateinit var cache: CachePort

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("redis.common.host") {
                RedisExtension.host()
            }
            registry.add("redis.common.port") {
                RedisExtension.port()
            }
        }
    }

    init {
        context("cache block") {
            should("store in redis") {
                cache.cache("key") { "computation" }.shouldBe("computation")
                delay(100.milliseconds)
                cache.cache("key") { "different" }.shouldBe("computation")

                eventually(3.seconds) {
                    cache.cache("key") { "different" }.shouldBe("different")
                }
            }
        }
    }

    @Configuration
    @ComponentScan
    internal class TestConfiguration {
        @Bean("testRedis")
        fun testRedis(
            objectMapper: ObjectMapper,
            template: ReactiveStringRedisTemplate,
        ): CachePort {
            return RedisCache(
                enabled = true,
                ttlCalculator = TtlCalculator(1.seconds, 2.seconds),
                redisTemplate = template,
                objectMapper = objectMapper,
            )
        }

        @Bean
        fun objectMapper(): ObjectMapper = ObjectMapper()
    }
}
