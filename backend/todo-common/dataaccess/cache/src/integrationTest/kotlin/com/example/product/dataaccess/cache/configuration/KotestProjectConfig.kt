package com.example.product.dataaccess.mongo.configuration

import com.example.product.dataaccess.cache.RedisExtension
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.spring.SpringExtension

object KotestProjectConfig : AbstractProjectConfig() {
    override fun extensions() = listOf(SpringExtension, RedisExtension)
}
