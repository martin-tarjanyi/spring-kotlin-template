package com.example.product.dataaccess.mongo

import io.kotest.extensions.testcontainers.AbstractContainerExtension
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName

object MongoKotestExtension : AbstractContainerExtension<MongoDBContainer>(
    container = MongoDBContainer(DockerImageName.parse("mongo:7.0")),
)
