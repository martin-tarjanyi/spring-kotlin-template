package com.example.product.dataaccess.mongo

import com.example.product.dataaccess.mongo.model.TodoData
import com.example.product.domain.model.Todo
import com.example.product.domain.port.out.TodoPersistencePort
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Component

@Component
internal class TodoDataMongoAdapter(private val mongoTemplate: ReactiveMongoTemplate) : TodoPersistencePort {
    override suspend fun save(todo: Todo) {
        val todoData = todo.toData()
        mongoTemplate.save(todoData).awaitSingleOrNull()
    }

    override suspend fun findById(id: String): Todo? =
        mongoTemplate.findById(id, TodoData::class.java)
            .awaitSingleOrNull()
            ?.toDomain()

    override suspend fun findAll(incompleteOnly: Boolean): List<Todo> =
        mongoTemplate.find<TodoData>(
            if (incompleteOnly) {
                Query(TodoData::completed isEqualTo false)
            } else {
                Query()
            },
        ).collectList()
            .awaitSingle()
            .map { it.toDomain() }
}

private fun TodoData.toDomain() =
    Todo(
        id = id,
        title = title,
        description = description,
        completed = completed,
    )

private fun Todo.toData() =
    TodoData(
        id = id,
        title = title,
        description = description,
        completed = completed,
    )
