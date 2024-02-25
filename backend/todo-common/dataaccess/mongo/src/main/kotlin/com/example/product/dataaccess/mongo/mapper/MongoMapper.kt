package com.example.product.dataaccess.mongo.mapper

import org.bson.types.ObjectId
import org.mapstruct.Mapper

@Mapper
interface MongoMapper {
    fun toDomainId(objectId: ObjectId): String {
        return objectId.toString()
    }

    fun toObjectId(id: String?): ObjectId {
        return id?.let { ObjectId(it) } ?: ObjectId()
    }
}
