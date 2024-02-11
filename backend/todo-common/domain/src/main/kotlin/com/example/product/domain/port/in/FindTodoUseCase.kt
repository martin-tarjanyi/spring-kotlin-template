package com.example.product.domain.port.`in`

import com.example.product.domain.model.Todo

interface FindTodoUseCase {
    suspend fun findAll(incompleteOnly: Boolean): List<Todo>
}
