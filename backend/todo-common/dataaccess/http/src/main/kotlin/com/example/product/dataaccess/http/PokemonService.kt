package com.example.product.dataaccess.http

import com.example.product.dataaccess.http.pokemon.PokemonApi
import com.example.product.domain.model.FictionalCharacter
import com.example.product.domain.port.out.FictionalUniversePort
import org.springframework.stereotype.Component

@Component
internal class PokemonService(private val pokemonApi: PokemonApi) : FictionalUniversePort {
    override suspend fun randomCreature() =
        pokemonApi.getPokemon((1..100).random())
            .let { FictionalCharacter(it.name) }
}
