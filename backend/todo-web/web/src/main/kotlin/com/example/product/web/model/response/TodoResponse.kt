package com.example.product.web.model.response

import com.example.product.web.configuration.InDevelopmentProperty
import java.time.Instant

data class TodoResponse(
    val id: String,
    val title: String,
    val description: String,
    val completed: Boolean,
    @InDevelopmentProperty
    val createdAt: Instant? = null,
)
