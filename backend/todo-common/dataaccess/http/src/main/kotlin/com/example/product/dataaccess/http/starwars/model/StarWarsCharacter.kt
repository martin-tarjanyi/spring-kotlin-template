package com.example.product.dataaccess.http.starwars.model

import com.fasterxml.jackson.annotation.JsonProperty

data class StarWarsCharacter(
    val name: String,
    val height: Int,
    @JsonProperty("birth_year") val birthYear: String,
)
