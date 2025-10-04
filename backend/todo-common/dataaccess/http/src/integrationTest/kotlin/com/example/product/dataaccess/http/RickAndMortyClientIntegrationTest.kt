package com.example.product.dataaccess.http

import com.example.product.dataaccess.http.WiremockExtension.wiremock
import com.example.product.dataaccess.http.rickandmorty.RickAndMortyGraphQlClient
import com.github.tomakehurst.wiremock.client.WireMock.and
import com.github.tomakehurst.wiremock.client.WireMock.containing
import com.github.tomakehurst.wiremock.client.WireMock.okJson
import com.github.tomakehurst.wiremock.client.WireMock.post
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired

class RickAndMortyClientIntegrationTest : BaseHttpIntegrationTest() {
    @Autowired
    private lateinit var client: RickAndMortyGraphQlClient

    init {
        context("findCharacterById") {
            should("return character") {
                wiremock.stubFor(
                    post("/graphql")
                        .withRequestBody(
                            and(
                                containing("character(id: \\\"1\\\")"),
                                containing("name"),
                                containing("episode"),
                            ),
                        ).willReturn(okJson(mockResponse().trimIndent())),
                )

                val character = client.findCharacterById("1")

                character shouldNotBe null
                character.id shouldBe "1"
                character.name shouldBe "Rick Sanchez"
                character.episode?.size shouldBe 2
                character.episode?.get(0)?.name shouldBe "Pilot"
            }
        }
    }

    private fun mockResponse(): String =
        """
        {
        "data": {
            "character": {
                "id": "1",
                "name": "Rick Sanchez",
                "episode": [
                    {
                        "id": "1",
                        "name": "Pilot"
                    },
                    {
                        "id": "2",
                        "name": "Lawnmower Dog"
                    }
                ]
            }
        }
        }
        """
}
