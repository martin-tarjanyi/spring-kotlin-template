package com.example.product.web.configuration

import io.swagger.v3.oas.models.security.OAuthFlow
import io.swagger.v3.oas.models.security.OAuthFlows
import io.swagger.v3.oas.models.security.Scopes
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springdoc.core.customizers.OperationCustomizer
import org.springdoc.core.customizers.PropertyCustomizer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import java.nio.charset.StandardCharsets

@Configuration
@EnableConfigurationProperties(ApiDocsProperties::class)
class SpringDocConfiguration {
    @Autowired
    lateinit var apiDocsProperties: ApiDocsProperties

    @Value("classpath:api_docs_v2_description.md")
    lateinit var resource: Resource

    @Bean
    fun openApiCustomizer(): OpenApiCustomizer =
        OpenApiCustomizer { openApi ->
            openApi.info.description(resource.getContentAsString(StandardCharsets.UTF_8))
            openApi.components
                .addSecuritySchemes("oauth2", buildOpenApiSecurityScheme())
                .addSecuritySchemes(
                    "accessToken",
                    SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("Bearer"),
                )
        }

    private fun buildOpenApiSecurityScheme(): SecurityScheme =
        SecurityScheme()
            .type(SecurityScheme.Type.OAUTH2)
            .description("Use test client ID and test secret")
            .flows(
                OAuthFlows()
                    .authorizationCode(
                        OAuthFlow()
                            .tokenUrl("/oauth/token/")
                            .authorizationUrl("/oauth/authorize")
                            .scopes(
                                Scopes()
                                    .addString("w:todo", "Write todo"),
                            ),
                    ).password(
                        OAuthFlow()
                            .tokenUrl("/oauth/token/")
                            .scopes(
                                Scopes()
                                    .addString("w:todo", "Write todo"),
                            ),
                    ),
            )

    @Bean
    fun operationEnvironmentCustomizer(): OperationCustomizer =
        OperationCustomizer { operation, handlerMethod ->
            if (handlerMethod.method.annotations.any { it.annotationClass == CustomerFacingOperation::class }) {
                operation
            } else if (apiDocsProperties.publishNonCustomerFacingEndpoints) {
                operation
            } else {
                null
            }
        }

    @Bean
    fun operationSecurityCustomizer(): OperationCustomizer =
        OperationCustomizer { operation, handlerMethod ->
            operation?.let {
                if (handlerMethod.method.annotations.any { it.annotationClass == NoSecurityRequirement::class }) {
                    operation.security(emptyList())
                } else if (operation.security.orEmpty().none { it.containsKey(SecuritySchemes.ACCESS_TOKEN) }) {
                    operation.security(operation.security.orEmpty() + SecurityRequirement().addList(SecuritySchemes.ACCESS_TOKEN))
                } else {
                    operation
                }
            }
        }

    @Bean
    fun propertyEnvironmentCustomizer(): PropertyCustomizer =
        PropertyCustomizer { property, type ->
            if (type.ctxAnnotations.any { it.annotationClass == InDevelopmentProperty::class } &&
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
annotation class CustomerFacingOperation

/**
 * This annotation is used to mark API request/response field that are under development and should be published in dev environments only.
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class InDevelopmentProperty

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class NoSecurityRequirement

@ConfigurationProperties(prefix = "api-docs")
data class ApiDocsProperties(
    val publishNonCustomerFacingEndpoints: Boolean = false,
    val publishInDevelopmentProperties: Boolean = false,
)

object SecuritySchemes {
    const val OAUTH2 = "oauth2"
    const val ACCESS_TOKEN = "accessToken"
}

object SecurityScopes {
    const val WRITE = "w:todo"
}
