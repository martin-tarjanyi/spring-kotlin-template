package com.example.product.dataaccess.mongo.extensions

import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.singleOrNull
import org.bson.types.ObjectId

suspend fun <T : Any> MongoCollection<T>.findById(id: String): T? {
    return this.find(eq("_id", ObjectId(id))).singleOrNull()
}
