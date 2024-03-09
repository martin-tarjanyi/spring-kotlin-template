package com.example.product.configuration

import com.example.product.dataaccess.mongo.MongoExtension
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.spring.SpringExtension

object KotestProjectConfig : AbstractProjectConfig() {
    override fun extensions() = listOf(SpringExtension, MongoExtension)
}
