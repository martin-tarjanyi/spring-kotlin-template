package com.example.product.dataaccess.mongo

import com.example.product.domain.model.SaveTodoCommand

fun saveTodoCommand(
    title: String = "title",
    description: String = "description",
    completed: Boolean = false,
) = SaveTodoCommand(
    title = title,
    description = description,
    completed = completed,
)
