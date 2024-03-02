package com.example.product.domain.port.inward

import com.example.product.domain.model.SaveTodoCommand
import com.example.product.domain.model.Todo

interface CreateTodoUseCase {
    suspend fun create(command: SaveTodoCommand): Todo
}
