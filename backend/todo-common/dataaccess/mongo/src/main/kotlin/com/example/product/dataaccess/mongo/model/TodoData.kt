package com.example.product.dataaccess.mongo.model

data class TodoData(
    val id: String,
    val title: String,
    val description: String,
    val completed: Boolean,
)
