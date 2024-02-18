package com.example.product.dataaccess.http.common

import io.github.oshai.kotlinlogging.KotlinLogging
import io.netty.channel.ChannelOption
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.time.Duration

@Component
class HttpClientFactory(private val webClientBuilder: WebClient.Builder) {
    private val logger = KotlinLogging.logger { }

    fun <T> create(
        clientProperties: HttpClientProperties,
        interfaceType: Class<T>,
    ): T {
        logger.info { "Creating HTTP client for interface ${interfaceType.simpleName} with properties $clientProperties" }
        val httpClient =
            HttpClient.create(
                ConnectionProvider.builder(interfaceType.simpleName)
                    .maxConnections(clientProperties.maxConnections)
                    .pendingAcquireTimeout(Duration.ofSeconds(3))
                    .build(),
            )
                .compress(true)
                .option(
                    ChannelOption.CONNECT_TIMEOUT_MILLIS,
                    clientProperties.connectionTimeout.toMillis().toInt(),
                )
                .responseTimeout(clientProperties.readTimeout)

        val webClient =
            webClientBuilder.baseUrl(clientProperties.baseUrl)
                .clientConnector(ReactorClientHttpConnector(httpClient))
                .build()

        val adapter = WebClientAdapter.create(webClient)
        val factory = HttpServiceProxyFactory.builderFor(adapter).build()

        return factory.createClient(interfaceType)
    }
}

inline fun <reified T> HttpClientFactory.create(clientProperties: HttpClientProperties): T = create(clientProperties, T::class.java)
