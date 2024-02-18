package com.example.product.dataaccess.http.common

import java.time.Duration

data class HttpClientProperties(
    val baseUrl: String,
    val connectionTimeout: Duration = Duration.ofSeconds(1),
    val readTimeout: Duration,
    val maxConnections: Int = 50,
)
