package com.example.product.domain.port.out

import com.example.product.domain.model.SpaceCharacter

interface SpaceOperaPort {
    suspend fun randomCharacter(): SpaceCharacter
}
