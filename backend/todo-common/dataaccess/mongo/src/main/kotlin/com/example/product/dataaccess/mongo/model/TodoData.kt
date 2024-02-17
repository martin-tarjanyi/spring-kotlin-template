package com.example.product.dataaccess.mongo.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class TodoData(
    @BsonId
    val id: ObjectId,
    val title: String,
    val description: String,
    val completed: Boolean,
)
