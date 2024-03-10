package com.example.product.domain.port.inward

import com.example.product.domain.model.Todo
import com.example.product.domain.model.TodoId

interface FindTodoUseCase {
    suspend fun findAll(incompleteOnly: Boolean): List<Todo>

    suspend fun findById(id: TodoId): Todo?
}
