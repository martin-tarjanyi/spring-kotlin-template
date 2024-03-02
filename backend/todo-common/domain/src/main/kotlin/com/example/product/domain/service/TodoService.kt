package com.example.product.domain.service

import com.example.product.domain.model.SaveTodoCommand
import com.example.product.domain.model.Todo
import com.example.product.domain.port.inward.CreateTodoUseCase
import com.example.product.domain.port.inward.FindTodoUseCase
import com.example.product.domain.port.out.TodoPersistencePort
import org.springframework.stereotype.Component

@Component
internal class TodoService(private val persistencePort: TodoPersistencePort) : FindTodoUseCase, CreateTodoUseCase {
    override suspend fun findAll(incompleteOnly: Boolean): List<Todo> {
        return persistencePort.findAll(incompleteOnly)
    }

    override suspend fun findById(id: String): Todo? {
        return persistencePort.findById(id)
    }

    override suspend fun create(command: SaveTodoCommand): Todo = persistencePort.save(command)
}
