package com.example.product.dataaccess.http.rickandmorty

import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange

@HttpExchange
interface RickAndMortyApi {
    @PostExchange
    suspend fun graphQl(
        @RequestHeader headers: MultiValueMap<String, String>,
        @RequestBody body: String,
    ): ResponseEntity<String>
}
