package com.example.product.dataaccess.cache

import com.example.product.dataaccess.cache.redis.RedisCache
import com.example.product.domain.port.out.CachePort
import com.example.product.domain.port.out.RandomTtlProvider
import com.example.product.domain.port.out.cache
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
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
        context("redis cache") {
            should("be redis cache") {
                cache.shouldBeInstanceOf<RedisCache>()
            }

            should("save to cache and return") {
                cache.cache("key") { "computation" }.shouldBe("computation")
                delay(100.milliseconds)

                // return previously cached value
                cache.cache("key") { "different" }.shouldBe("computation")

                // return new value after expiration
                eventually(3.seconds) {
                    cache.cache("key") { "different" }.shouldBe("different")
                }
            }

            should("work with generics") {
                cache.cache("generics") { listOf(TestValue("value")) }
                    .shouldBe(listOf(TestValue("value")))

                delay(100.milliseconds)

                // return previously cached value
                cache.cache("generics") { listOf(TestValue("different")) }
                    .shouldBe(listOf(TestValue("value")))
            }

            should("ignore cache value if has different type") {
                cache.cache("ignore") { "test" }
                    .shouldBe("test")

                delay(100.milliseconds)

                // ignore cached value due to deserialization error
                cache.cache("ignore") { TestValue("hello") }
                    .shouldBe(TestValue("hello"))
                // return new cached value
                cache.cache("ignore") { TestValue("different") }
                    .shouldBe(TestValue("hello"))
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
                defaultTtlProvider = RandomTtlProvider(1.seconds, 2.seconds),
                redisTemplate = template,
                objectMapper = objectMapper,
            )
        }

        @Bean
        fun objectMapper(): ObjectMapper =
            ObjectMapper()
                .registerKotlinModule()
    }
}

private data class TestValue(val value: String)
