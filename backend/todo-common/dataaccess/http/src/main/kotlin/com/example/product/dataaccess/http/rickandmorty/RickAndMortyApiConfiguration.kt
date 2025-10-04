package com.example.product.dataaccess.http.rickandmorty

import com.example.product.dataaccess.http.common.HttpClientFactory
import com.example.product.dataaccess.http.common.HttpClientProperties
import com.example.product.dataaccess.http.common.create
import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.graphql.dgs.client.CustomMonoGraphQLClient
import com.netflix.graphql.dgs.client.HttpResponse
import kotlinx.coroutines.reactor.mono
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.http.HttpHeaders
import org.springframework.util.CollectionUtils

@Configuration
@EnableConfigurationProperties(RickAndMortyApiProperties::class)
@PropertySource("classpath:rick-and-morty-api.properties")
class RickAndMortyApiConfiguration {
    @Bean
    fun rickAndMortyGraphQlClient(
        properties: RickAndMortyApiProperties,
        rickAndMortyApi: RickAndMortyApi,
        mapper: ObjectMapper,
    ): RickAndMortyGraphQlClient {
        val client = CustomMonoGraphQLClient(
            url = properties.http.baseUrl,
            mapper = mapper,
            monoRequestExecutor = { _, headers, body ->
                mono {
                    val response = rickAndMortyApi.graphQl(
                        headers = HttpHeaders(CollectionUtils.toMultiValueMap(headers)),
                        body = body,
                    )
                    HttpResponse(
                        statusCode = response.statusCode.value(),
                        body = response.body,
                        headers = response.headers,
                    )
                }
            },
        )
        return RickAndMortyGraphQlClient(client)
    }

    @Bean
    fun rickAndMortyApi(
        httpClientFactory: HttpClientFactory,
        properties: RickAndMortyApiProperties,
    ): RickAndMortyApi = httpClientFactory.create(properties.http)
}

@ConfigurationProperties("rick-and-morty-api")
data class RickAndMortyApiProperties(
    val http: HttpClientProperties,
)
