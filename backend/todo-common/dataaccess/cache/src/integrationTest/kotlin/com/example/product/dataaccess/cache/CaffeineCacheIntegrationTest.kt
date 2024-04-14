package com.example.product.dataaccess.cache

import com.example.product.dataaccess.cache.caffeine.CaffeineCache
import com.example.product.domain.port.out.CachePort
import com.example.product.domain.port.out.TtlCalculator
import com.example.product.domain.port.out.cache
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
import org.springframework.test.context.ActiveProfiles
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@SpringBootTest
@ActiveProfiles("test")
class CaffeineCacheIntegrationTest : ShouldSpec() {
    @Autowired
    @Qualifier("testCaffeine")
    private lateinit var cache: CachePort

    init {
        context("cache block") {
            should("store in caffeine") {
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
        @Bean("testCaffeine")
        fun testCaffeine(): CachePort =
            CaffeineCache(
                enabled = true,
                ttlCalculator = TtlCalculator(1.seconds, 2.seconds),
            )
    }
}
