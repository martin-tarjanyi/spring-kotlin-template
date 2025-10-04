package com.example.product.dataaccess.http.rickandmorty

import com.netflix.graphql.dgs.client.MonoGraphQLClient
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableConfigurationProperties(RickAndMortyApiProperties::class)
@PropertySource("classpath:rick-and-morty-api.properties")
class RickAndMortyApiConfiguration {
    @Bean
    fun rickAndMortyApi(properties: RickAndMortyApiProperties): RickAndMortyApi {
        val client = MonoGraphQLClient.createWithWebClient(WebClient.create(properties.url))
        return RickAndMortyGraphQlClient(client)
    }
}

@ConfigurationProperties("rick-and-morty-api")
data class RickAndMortyApiProperties(
    val url: String,
)
