package com.example.product.domain.port.out

import com.example.product.domain.model.Todo

interface TodoPersistencePort {
    suspend fun save(todo: Todo)

    suspend fun findById(id: String): Todo?

    suspend fun findAll(incompleteOnly: Boolean): List<Todo>
}
