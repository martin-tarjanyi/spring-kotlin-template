package com.example.product.web.configuration

import org.springdoc.core.customizers.OpenApiCustomizer
import org.springdoc.core.customizers.OperationCustomizer
import org.springdoc.core.customizers.PropertyCustomizer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(ApiDocsProperties::class)
class SpringDocConfiguration {
    @Autowired
    lateinit var apiDocsProperties: ApiDocsProperties

    @Bean
    fun operationEnvironmentCustomizer(): OperationCustomizer =
        OperationCustomizer { operation, handlerMethod ->
            if (handlerMethod.method.annotations.any { it.annotationClass == OpenApiCustomerFacingEndpoint::class }) {
                operation
            } else if (apiDocsProperties.publishNonCustomerFacingEndpoints) {
                operation
            } else {
                null
            }
        }

    @Bean
    fun propertyEnvironmentCustomizer(): PropertyCustomizer =
        PropertyCustomizer { property, type ->
            if (type.ctxAnnotations.any { it.annotationClass == OpenApiInDevelopmentProperty::class } &&
                !apiDocsProperties.publishInDevelopmentProperties
            ) {
                null
            } else {
                property
            }
        }

    @Bean
    fun openApiCleanupCustomizer(): OpenApiCustomizer =
        OpenApiCustomizer { openApi ->
            // cleanup empty paths
            openApi.paths
                .filterValues { it.readOperations().isEmpty() }
                .forEach { openApi.paths.remove(it.key) }

            // cleanup unused tags
            val usedTags = openApi.paths
                .flatMap { it.value.readOperations() }
                .flatMap { it.tags }
                .toSet()

            openApi.tags
                .filter { !usedTags.contains(it.name) }
                .forEach { openApi.tags.remove(it) }

            openApi
        }
}

/**
 * This annotation is used to mark API endpoints that are ready to be published to customers on all environments in API docs.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OpenApiCustomerFacingEndpoint

/**
 * This annotation is used to mark API request/response field that are under development and should be published in dev environments only.
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class OpenApiInDevelopmentProperty

@ConfigurationProperties(prefix = "api-docs")
data class ApiDocsProperties(
    val publishNonCustomerFacingEndpoints: Boolean = false,
    val publishInDevelopmentProperties: Boolean = false,
)
