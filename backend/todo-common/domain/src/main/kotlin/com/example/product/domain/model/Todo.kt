package com.example.product.domain.model

data class Todo(
    val id: String,
    val title: String,
    val description: String,
    val completed: Boolean,
)

data class SaveTodo(
    val title: String,
    val description: String,
    val completed: Boolean,
)
