package com.example.product.dataaccess.http

import com.example.product.dataaccess.http.WiremockExtension.wiremock
import com.example.product.dataaccess.http.starwars.StarWarsApi
import com.example.product.dataaccess.http.starwars.model.StarWarsCharacter
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.notFound
import com.github.tomakehurst.wiremock.client.WireMock.okJson
import com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestPropertySource
import kotlin.time.Duration.Companion.seconds

@SpringBootTest
@EnableAutoConfiguration
@TestPropertySource(properties = ["star-wars-api.http.readTimeout=1s"])
@ActiveProfiles("test")
class StarWarsApiIntegrationTest : ShouldSpec() {
    @Autowired
    private lateinit var starWarsApi: StarWarsApi

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    override fun extensions(): List<Extension> = listOf(SpringExtension)

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("star-wars-api.http.baseUrl") {
                "http://localhost:${WiremockExtension.port()}"
            }
        }
    }

    init {
        context("Get person") {
            should("return person") {
                val stub =
                    StarWarsCharacter(
                        name = "Luke Skywalker",
                        height = 160,
                        birthYear = "19BY",
                    )

                wiremock.stubFor(
                    get(urlMatching("/people/25")).willReturn(
                        okJson("").withBody(objectMapper.writeValueAsString(stub)),
                    ),
                )

                val character = starWarsApi.getPerson(25)

                character.name shouldBe "Luke Skywalker"
            }

            should("not find a person") {
                wiremock.stubFor(
                    get(urlMatching("/people/404")).willReturn(
                        notFound().withBody("""{ "detail": "Not found"}"""),
                    ),
                )

                shouldThrowAny {
                    starWarsApi.getPerson(404)
                }
            }

            should("time out") {
                val stub =
                    StarWarsCharacter(
                        name = "Luke Skywalker",
                        height = 160,
                        birthYear = "19BY",
                    )

                wiremock.stubFor(
                    get(urlMatching("/people/25")).willReturn(
                        okJson("").withBody(objectMapper.writeValueAsString(stub))
                            .withFixedDelay(5.seconds.inWholeMilliseconds.toInt()),
                    ),
                )

                shouldThrowAny {
                    starWarsApi.getPerson(25)
                }
            }
        }
    }

    @Configuration
    @ComponentScan
    internal class TestConfig
}
