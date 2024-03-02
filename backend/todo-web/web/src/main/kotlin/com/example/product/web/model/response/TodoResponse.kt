package com.example.product.web.model.response

data class TodoResponse(
    val id: String,
    val title: String,
    val description: String,
    val completed: Boolean,
)
