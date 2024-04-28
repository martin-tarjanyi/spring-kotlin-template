package com.example.product.dataaccess.http.pokemon

import com.example.product.dataaccess.http.common.HttpClientFactory
import com.example.product.dataaccess.http.common.HttpClientProperties
import com.example.product.dataaccess.http.common.create
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource("classpath:pokemon-api.properties")
@EnableConfigurationProperties(PokemonApiProperties::class)
class PokemonApiConfiguration {
    @Bean
    fun pokemonApi(
        httpClientFactory: HttpClientFactory,
        properties: PokemonApiProperties,
    ): PokemonApi = httpClientFactory.create(properties.http)
}

@ConfigurationProperties("pokemon-api")
data class PokemonApiProperties(
    val http: HttpClientProperties,
)
