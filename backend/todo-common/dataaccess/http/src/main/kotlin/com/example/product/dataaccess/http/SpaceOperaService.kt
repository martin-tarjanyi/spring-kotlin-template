package com.example.product.dataaccess.http

import com.example.product.dataaccess.http.starwars.StarWarsApi
import com.example.product.domain.model.SpaceCharacter
import com.example.product.domain.port.out.SpaceOperaPort
import org.springframework.stereotype.Component

@Component
internal class SpaceOperaService(private val starWarsApi: StarWarsApi) : SpaceOperaPort {
    override suspend fun randomCharacter() =
        starWarsApi.getPerson((1..100).random())
            .let { SpaceCharacter(it.name) }
}
