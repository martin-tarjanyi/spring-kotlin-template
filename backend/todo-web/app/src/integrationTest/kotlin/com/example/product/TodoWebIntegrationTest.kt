package com.example.product

import com.example.product.dataaccess.mongo.saveTodoCommand
import com.example.product.domain.model.TodoId
import com.example.product.domain.port.out.TodoPersistencePort
import com.example.product.web.model.request.CreateTodoRequest
import com.example.product.web.model.response.TodoResponse
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.expectBody

class TodoWebIntegrationTest : BaseWebIntegrationTest() {
    @Autowired
    private lateinit var todoPersistencePort: TodoPersistencePort

    init {
        context("GET /todos") {
            beforeAny {
                todoPersistencePort.save(saveTodoCommand(completed = true))
                todoPersistencePort.save(saveTodoCommand(completed = false))
                todoPersistencePort.save(saveTodoCommand(completed = false))
            }

            should("return incomplete todos") {
                // WHEN-THEN
                val result = webTestClient.get().uri("/todos")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody<List<TodoResponse>>()
                    .returnResult()
                    .responseBody

                result.shouldNotBeEmpty()
                    .shouldHaveSize(2)
            }

            should("return all todos") {
                // WHEN-THEN
                val result = webTestClient.get().uri {
                    it.path("/todos")
                        .queryParam("incompleteOnly", "false")
                        .build()
                }
                    .exchange()
                    .expectStatus().isOk
                    .expectBody<List<TodoResponse>>()
                    .returnResult()
                    .responseBody

                result.shouldNotBeEmpty()
                    .shouldHaveSize(3)
            }
        }

        context("GET /todos/{id}") {
            should("find by existing ID") {
                // GIVEN
                val todo = todoPersistencePort.save(saveTodoCommand())

                // WHEN-THEN
                val result = webTestClient.get().uri { it.path("/todos/{id}").build(todo.id.value) }
                    .exchange()
                    .expectStatus().isOk
                    .expectBody<TodoResponse>()
                    .returnResult()
                    .responseBody

                result.shouldNotBeNull()
            }

            should("not find by not existing ID") {
                // GIVEN
                val id = "507f1f77bcf86cd799439011"

                // WHEN-THEN
                webTestClient.get().uri { it.path("/todos/{id}").build(id) }
                    .exchange()
                    .expectStatus().isNotFound
            }
        }

        context("POST /todos") {
            should("create a todo") {
                // GIVEN
                val request = CreateTodoRequest(
                    title = "title",
                    description = "description",
                )

                // WHEN-THEN
                val result = webTestClient.post().uri("/todos")
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated
                    .expectBody<TodoResponse>()
                    .returnResult().responseBody

                val id = result?.shouldNotBeNull()
                    ?.id.shouldNotBeNull()
                todoPersistencePort.findById(TodoId(id)).shouldNotBeNull()
            }
        }
    }
}
