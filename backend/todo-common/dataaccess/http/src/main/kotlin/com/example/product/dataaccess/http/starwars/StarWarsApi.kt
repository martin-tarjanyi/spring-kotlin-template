package com.example.product.dataaccess.http.starwars

import com.example.product.dataaccess.http.starwars.model.StarWarsCharacter
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange

@HttpExchange
interface StarWarsApi {
    @GetExchange("/people/{id}")
    suspend fun getPerson(
        @PathVariable id: Int,
    ): StarWarsCharacter
}
