package com.example.product.dataaccess.http.common

import io.github.oshai.kotlinlogging.KotlinLogging
import io.netty.channel.ChannelOption
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.CoExchangeFilterFunction
import org.springframework.web.reactive.function.client.CoExchangeFunction
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
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
        logger.atInfo {
            message = "Creating HTTP client"
            payload = mapOf("interface" to interfaceType.simpleName, "properties" to clientProperties)
        }
        val httpClient = HttpClient.create(
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

        val webClient = webClientBuilder.clone()
            .baseUrl(clientProperties.baseUrl)
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .filter(HttpLoggerFilter)
            .build()

        val adapter = WebClientAdapter.create(webClient)
        val factory = HttpServiceProxyFactory.builderFor(adapter).build()

        return factory.createClient(interfaceType)
    }
}

inline fun <reified T> HttpClientFactory.create(clientProperties: HttpClientProperties): T = create(clientProperties, T::class.java)

private object HttpLoggerFilter : CoExchangeFilterFunction() {
    private val logger = KotlinLogging.logger("http-call")

    override suspend fun filter(
        request: ClientRequest,
        next: CoExchangeFunction,
    ): ClientResponse {
        val logProperties = mutableMapOf<String, Any>()
        val start = System.currentTimeMillis()

        try {
            logProperties += listOf("method" to request.method().name(), "url" to request.url())
            val clientResponse = next.exchange(request)
            logProperties += listOf("status" to clientResponse.statusCode().value())
            return clientResponse
        } catch (e: Exception) {
            logProperties += errorProperties(e)
            throw e
        } finally {
            logger.atInfo {
                message = "HTTP Call"
                payload = logProperties + mapOf("durationInMillis" to System.currentTimeMillis() - start)
            }
        }
    }

    private fun errorProperties(e: Exception): List<Pair<String, String>> {
        val exception = when {
            e is WebClientRequestException -> e.cause ?: e
            else -> e
        }

        return listOfNotNull(
            "exception" to exception.javaClass.getSimpleName(),
            (e.message ?: e.cause?.message)?.let { "exceptionMessage" to it },
        )
    }
}
