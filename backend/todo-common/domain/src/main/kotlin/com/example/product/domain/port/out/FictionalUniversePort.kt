package com.example.product.domain.port.out

import com.example.product.domain.model.FictionalCharacter

interface FictionalUniversePort {
    suspend fun randomCreature(): FictionalCharacter
}
