package com.example.product.dataaccess.http.configuration

import com.example.product.dataaccess.http.WiremockExtension
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.spring.SpringExtension

object KotestProjectConfig : AbstractProjectConfig() {
    override fun extensions() = listOf(SpringExtension, WiremockExtension)
}
