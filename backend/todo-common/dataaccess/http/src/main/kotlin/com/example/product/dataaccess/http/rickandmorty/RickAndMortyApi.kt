package com.example.product.dataaccess.http.rickandmorty

import com.example.rickmorty.generated.types.Character

interface RickAndMortyApi {
    suspend fun findCharacterById(id: String): Character
}
