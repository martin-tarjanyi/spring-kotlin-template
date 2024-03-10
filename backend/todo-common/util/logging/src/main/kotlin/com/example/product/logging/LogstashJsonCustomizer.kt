package com.example.product.logging

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import net.logstash.logback.decorate.JsonFactoryDecorator
import org.bson.types.ObjectId

class LogstashJsonCustomizer : JsonFactoryDecorator {
    override fun decorate(factory: JsonFactory): JsonFactory {
        val codec = factory.codec
        if (codec is ObjectMapper) {
            codec.registerModule(
                SimpleModule().apply {
                    addSerializer(ObjectId::class.java, ObjectIdSerializer)
                },
            )
        }
        return factory
    }
}

private object ObjectIdSerializer : JsonSerializer<ObjectId>() {
    override fun serialize(
        value: ObjectId,
        gen: JsonGenerator,
        serializers: SerializerProvider,
    ) {
        gen.writeString(value.toHexString())
    }
}
