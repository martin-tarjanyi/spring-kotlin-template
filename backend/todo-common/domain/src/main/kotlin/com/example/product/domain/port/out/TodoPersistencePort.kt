package com.example.product.domain.port.out

import com.example.product.domain.model.SaveTodoCommand
import com.example.product.domain.model.Todo

interface TodoPersistencePort {
    suspend fun save(command: SaveTodoCommand): Todo

    suspend fun findById(id: String): Todo?

    suspend fun findAll(incompleteOnly: Boolean): List<Todo>
}
