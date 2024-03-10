package com.example.product.domain.model

import com.example.product.domain.util.DomainId

data class Todo(
    val id: TodoId,
    val title: String,
    val description: String,
    val completed: Boolean,
)

data class SaveTodoCommand(
    val title: String,
    val description: String,
    val completed: Boolean,
)

@JvmInline
value class TodoId(override val value: String) : DomainId
