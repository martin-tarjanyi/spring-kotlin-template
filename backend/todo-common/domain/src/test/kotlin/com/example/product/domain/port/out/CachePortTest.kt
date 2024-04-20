package com.example.product.domain.port.out

import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class CachePortTest : ShouldSpec() {
    init {
        context("cache block") {
            should("compute and use cache after that") {
                val testCache = TestCache()
                val result1 = testCache.cache("key") {
                    "computation"
                }
                val result2 = testCache.cache("key2") {
                    "computation2"
                }

                result1 shouldBe "computation"
                result2 shouldBe "computation2"

                eventually(100.milliseconds) {
                    testCache.cache<String>("key") { throw IllegalStateException("should not be called") } shouldBe "computation"
                    testCache.cache<String>("key2") { throw IllegalStateException("should not be called") } shouldBe "computation2"
                }
            }

            should("compute value again if cache get fails") {
                val testCache = TestCache(failOnGet = true)
                testCache.cache("key") { "computation" } shouldBe "computation"
                testCache.cache("key") { "computation2" } shouldBe "computation2"
            }

            should("return value again if cache set fails") {
                val testCache = TestCache(failOnSet = true)
                testCache.cache("key") { "computation" } shouldBe "computation"
                testCache.cache("key") { "computation2" } shouldBe "computation2"
            }

            should("set cache value async") {
                val testCache = TestCache(delayOnSet = 100.milliseconds)
                testCache.cache("key") { "computation" } shouldBe "computation"
                testCache.get<String>("key", jacksonTypeRef()).shouldBeNull()

                eventually(500.milliseconds) {
                    testCache.get<String>("key", jacksonTypeRef()) shouldBe "computation"
                }
            }

            should("not cache null value") {
                val testCache = TestCache()

                testCache.cache<String?>("key") { null }.shouldBeNull()
                delay(1.milliseconds)
                testCache.cache<String?>("key") { "not_null" }.shouldNotBeNull()
            }
        }

        context("random ttl provider") {
            should("return random ttl") {
                val ttlProvider = RandomTtlProvider(5.seconds, 10.seconds)
                ttlProvider.ttl().should {
                    it.shouldBeGreaterThanOrEqualTo(5.seconds)
                    it.shouldBeLessThanOrEqualTo(10.seconds)
                }
            }
        }
    }
}
