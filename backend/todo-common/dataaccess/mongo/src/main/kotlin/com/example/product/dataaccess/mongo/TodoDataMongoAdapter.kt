package com.example.product.dataaccess.mongo

import com.example.product.dataaccess.mongo.extensions.findById
import com.example.product.dataaccess.mongo.mapper.toData
import com.example.product.dataaccess.mongo.mapper.toDomain
import com.example.product.dataaccess.mongo.model.TodoData
import com.example.product.domain.model.SaveTodoCommand
import com.example.product.domain.model.Todo
import com.example.product.domain.model.TodoId
import com.example.product.domain.port.out.TodoPersistencePort
import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.toList
import org.bson.BsonDocument
import org.springframework.stereotype.Component

@Component
internal class TodoDataMongoAdapter(mongoDatabase: MongoDatabase) : TodoPersistencePort {
    private val collection = mongoDatabase.getCollection<TodoData>("Todo")

    override suspend fun save(command: SaveTodoCommand): Todo {
        val todoData = command.toData()
        collection.insertOne(todoData)
        return todoData.toDomain()
    }

    override suspend fun findById(id: TodoId): Todo? = collection.findById(id.value)?.toDomain()

    override suspend fun findAll(incompleteOnly: Boolean): List<Todo> =
        collection.find(
            if (incompleteOnly) {
                eq(TodoData::completed.name, false)
            } else {
                BsonDocument()
            },
        ).toList().map { it.toDomain() }
}
