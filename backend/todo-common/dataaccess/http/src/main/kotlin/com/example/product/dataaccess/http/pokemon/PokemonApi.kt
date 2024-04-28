package com.example.product.dataaccess.http.pokemon

import com.example.product.dataaccess.http.pokemon.model.PokemonCharacter
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange

@HttpExchange
interface PokemonApi {
    @GetExchange("pokemon/{id}")
    suspend fun getPokemon(
        @PathVariable id: Int,
    ): PokemonCharacter
}
