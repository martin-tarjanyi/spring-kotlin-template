package com.example.product.domain.port.out

import com.example.product.domain.model.SaveTodoCommand
import com.example.product.domain.model.Todo
import com.example.product.domain.model.TodoId

interface TodoPersistencePort {
    suspend fun save(command: SaveTodoCommand): Todo

    suspend fun findById(id: TodoId): Todo?

    suspend fun findAll(incompleteOnly: Boolean): List<Todo>
}
