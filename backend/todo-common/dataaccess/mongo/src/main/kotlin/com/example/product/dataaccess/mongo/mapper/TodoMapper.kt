package com.example.product.dataaccess.mongo.mapper

import com.example.product.dataaccess.mongo.model.TodoData
import com.example.product.domain.model.SaveTodoCommand
import com.example.product.domain.model.Todo
import com.example.product.domain.model.TodoId

fun SaveTodoCommand.toData(): TodoData {
    return TodoData(
        title = this.title,
        description = this.description,
        completed = this.completed,
    )
}

fun TodoData.toDomain(): Todo {
    return Todo(
        id = TodoId(this.id.toHexString()),
        title = this.title,
        description = this.description,
        completed = this.completed,
    )
}
