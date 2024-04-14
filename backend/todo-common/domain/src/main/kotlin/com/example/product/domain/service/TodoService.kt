package com.example.product.domain.service

import com.example.product.domain.model.SaveTodoCommand
import com.example.product.domain.model.Todo
import com.example.product.domain.model.TodoId
import com.example.product.domain.port.inward.CreateTodoUseCase
import com.example.product.domain.port.inward.FindTodoUseCase
import com.example.product.domain.port.out.CachePort
import com.example.product.domain.port.out.TodoPersistencePort
import com.example.product.domain.port.out.cache
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
internal class TodoService(
    private val persistencePort: TodoPersistencePort,
    @Qualifier("inMemory") private val cache: CachePort,
) : FindTodoUseCase, CreateTodoUseCase {
    override suspend fun findAll(incompleteOnly: Boolean): List<Todo> {
        return cache.cache("todo-all-$incompleteOnly") {
            persistencePort.findAll(incompleteOnly)
        }
    }

    override suspend fun findById(id: TodoId): Todo? {
        return persistencePort.findById(id)
    }

    override suspend fun create(command: SaveTodoCommand): Todo = persistencePort.save(command)
}
