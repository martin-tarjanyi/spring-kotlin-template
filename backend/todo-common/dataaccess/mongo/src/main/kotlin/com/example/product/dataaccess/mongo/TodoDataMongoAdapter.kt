package com.example.product.dataaccess.mongo

import com.example.product.dataaccess.mongo.mapper.TodoMapper
import com.example.product.dataaccess.mongo.model.TodoData
import com.example.product.domain.model.SaveTodo
import com.example.product.domain.model.Todo
import com.example.product.domain.port.out.TodoPersistencePort
import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.bson.BsonDocument
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Component

@Component
internal class TodoDataMongoAdapter(mongoDatabase: MongoDatabase) : TodoPersistencePort {
    private val collection = mongoDatabase.getCollection<TodoData>("Todo")
    private val mapper = Mappers.getMapper(TodoMapper::class.java)

    override suspend fun save(todo: SaveTodo): Todo {
        val todoData = mapper.toData(todo)
        collection.insertOne(todoData)
        return mapper.toDomain(todoData)
    }

    override suspend fun findById(id: String): Todo? =
        collection.find(eq(TodoData::id.name, id))
            .singleOrNull()
            ?.let { mapper.toDomain(it) }

    override suspend fun findAll(incompleteOnly: Boolean): List<Todo> =
        collection.find(
            if (incompleteOnly) {
                eq(TodoData::completed.name, false)
            } else {
                BsonDocument()
            },
        ).toList().map { mapper.toDomain(it) }
}
