package com.example.product.dataaccess.mongo

import com.mongodb.kotlin.client.coroutine.MongoClient
import io.kotest.core.listeners.AfterEachListener
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import org.bson.BsonDocument
import org.testcontainers.containers.MongoDBContainer

object MongoExtension : BeforeProjectListener, AfterProjectListener, AfterEachListener {
    private val container =
        MongoDBContainer("mongo:7.0")
            .withReuse(true)

    private lateinit var client: MongoClient

    override suspend fun beforeProject() {
        if (!container.isRunning) {
            container.start()
        }
        client = MongoClient.create(container.connectionString)
    }

    override suspend fun afterProject() {
        client.close()
    }

    override suspend fun afterEach(
        testCase: TestCase,
        result: TestResult,
    ) {
        val database = client.getDatabase("test")
        database.listCollectionNames()
            .collect { database.getCollection<Any>(it).deleteMany(BsonDocument()) }
    }

    fun connectionString(): String = container.connectionString
}
