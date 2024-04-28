package com.example.product.dataaccess.http

import com.example.product.dataaccess.http.WiremockExtension.wiremock
import com.example.product.dataaccess.http.pokemon.PokemonApi
import com.example.product.dataaccess.http.pokemon.model.PokemonCharacter
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.notFound
import com.github.tomakehurst.wiremock.client.WireMock.okJson
import com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired

class PokemonApiIntegrationTest : BaseHttpIntegrationTest() {
    @Autowired
    private lateinit var pokemonApi: PokemonApi

    init {
        context("Get pokemon") {
            should("return pokemon") {
                val stub =
                    PokemonCharacter(
                        name = "pikachu",
                        height = 4,
                        weight = 60,
                    )

                wiremock.stubFor(
                    get(urlMatching("/api/v2/pokemon/25")).willReturn(
                        okJson("").withBody(objectMapper.writeValueAsString(stub)),
                    ),
                )

                val character = pokemonApi.getPokemon(25)

                character.name shouldBe "pikachu"
            }

            should("not find a pokemon") {
                wiremock.stubFor(
                    get(urlMatching("/api/v2/pokemon/1500")).willReturn(
                        notFound().withBody("Not Found"),
                    ),
                )

                shouldThrowAny {
                    pokemonApi.getPokemon(1500)
                }
            }
        }
    }
}
