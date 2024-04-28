package com.example.product.dataaccess.http

import com.example.product.dataaccess.http.WiremockExtension.wiremock
import com.example.product.dataaccess.http.starwars.StarWarsApi
import com.example.product.dataaccess.http.starwars.model.StarWarsCharacter
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.notFound
import com.github.tomakehurst.wiremock.client.WireMock.okJson
import com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import kotlin.time.Duration.Companion.seconds

class StarWarsApiIntegrationTest : BaseHttpIntegrationTest() {
    @Autowired
    private lateinit var starWarsApi: StarWarsApi

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
}
