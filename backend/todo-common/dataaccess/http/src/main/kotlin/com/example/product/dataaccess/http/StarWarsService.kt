package com.example.product.dataaccess.http

import com.example.product.dataaccess.http.starwars.StarWarsApi
import com.example.product.domain.model.FictionalCharacter
import com.example.product.domain.port.out.FictionalUniversePort
import org.springframework.stereotype.Component

@Component
internal class StarWarsService(private val starWarsApi: StarWarsApi) : FictionalUniversePort {
    override suspend fun randomCharacter() =
        starWarsApi.getPerson((1..100).random())
            .let { FictionalCharacter(it.name) }
}
