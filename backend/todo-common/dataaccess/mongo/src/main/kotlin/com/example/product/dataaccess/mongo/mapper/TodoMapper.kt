package com.example.product.dataaccess.mongo.mapper

import com.example.product.dataaccess.mongo.model.TodoData
import com.example.product.domain.model.SaveTodo
import com.example.product.domain.model.Todo
import org.bson.types.ObjectId
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(uses = [MongoMapper::class])
internal interface TodoMapper {
    @Mapping(target = "extraData", ignore = true)
    @Mapping(source = "objectId", target = "id")
    fun toData(
        saveTodo: SaveTodo,
        objectId: ObjectId = ObjectId(),
    ): TodoData

    fun toDomain(todoData: TodoData): Todo
}
