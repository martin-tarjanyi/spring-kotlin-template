package com.example.product.dataaccess.mongo.configuration

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class MongoSpringConfiguration {
    @Value("\${MONGO_CONNECTION_URI:}")
    private lateinit var mongoConnectionUri: String

    private val logger = KotlinLogging.logger {}

    @Bean
    fun mongoDatabase(client: MongoClient): MongoDatabase {
        return client.getDatabase("test")
    }

    @Bean
    fun mongoClient(): MongoClient = MongoClient.create(mongoConnectionUri)
}
