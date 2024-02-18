package com.example.product.dataaccess.http.starwars

import com.example.product.dataaccess.http.common.HttpClientFactory
import com.example.product.dataaccess.http.common.HttpClientProperties
import com.example.product.dataaccess.http.common.create
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource("classpath:star-wars-api.properties")
@EnableConfigurationProperties(StarWarsApiProperties::class)
class StarWarsApiConfiguration {
    @Bean
    fun starWarsApi(
        httpClientFactory: HttpClientFactory,
        properties: StarWarsApiProperties,
    ): StarWarsApi = httpClientFactory.create(properties.http)
}

@ConfigurationProperties("star-wars-api")
data class StarWarsApiProperties(
    val http: HttpClientProperties,
)
