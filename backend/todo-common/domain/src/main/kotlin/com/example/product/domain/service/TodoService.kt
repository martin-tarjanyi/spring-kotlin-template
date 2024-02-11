package com.example.product.domain.service

import com.example.product.domain.model.Todo
import com.example.product.domain.port.`in`.FindTodoUseCase
import com.example.product.domain.port.out.TodoPersistencePort
import org.springframework.stereotype.Component

@Component
internal class TodoService(private val persistencePort: TodoPersistencePort) : FindTodoUseCase {
    override suspend fun findAll(incompleteOnly: Boolean): List<Todo> {
        return persistencePort.findAll(incompleteOnly)
    }
}
