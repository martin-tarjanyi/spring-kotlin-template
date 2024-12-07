package com.example.product

import org.mockito.BDDMockito.given

class OpenApiWebIntegrationTest : BaseWebIntegrationTest() {
    init {
        context("API docs") {
            should("exclude dev endpoints and properties") {
                given(apiDocsProperties.publishNonCustomerFacingEndpoints).willReturn(false)
                given(apiDocsProperties.publishInDevelopmentProperties).willReturn(false)

                webTestClient.get().uri("/v3/api-docs")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody()
                    .jsonPath("$.paths./todos.post").exists()
                    .jsonPath("$.paths./log.get").doesNotExist()
                    .jsonPath("$.components.schemas.TodoResponse.properties.createdAt").doesNotExist()
            }

            should("include dev endpoints and properties") {
                given(apiDocsProperties.publishNonCustomerFacingEndpoints).willReturn(true)
                given(apiDocsProperties.publishInDevelopmentProperties).willReturn(true)

                webTestClient.get().uri("/v3/api-docs")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody()
                    .jsonPath("$.paths./todos.post").exists()
                    .jsonPath("$.paths./log.get").exists()
                    .jsonPath("$.components.schemas.TodoResponse.properties.createdAt").exists()
            }
        }
    }
}
