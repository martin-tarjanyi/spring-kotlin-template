package com.example.product.web.controller

import com.example.product.domain.model.SaveTodoCommand
import com.example.product.domain.model.Todo
import com.example.product.domain.model.TodoId
import com.example.product.domain.port.inward.CreateTodoUseCase
import com.example.product.domain.port.inward.FindTodoUseCase
import com.example.product.web.error.NotFoundException
import com.example.product.web.model.request.CreateTodoRequest
import com.example.product.web.model.request.TodoQueryParameters
import com.example.product.web.model.response.TodoResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Todo", description = "Todo task management endpoints")
class TodoController(val findTodoUseCase: FindTodoUseCase, val createTodoUseCase: CreateTodoUseCase) {
    @GetMapping("/todos/{id}")
    suspend fun getTodo(
        @PathVariable("id") id: String,
    ): TodoResponse =
        findTodoUseCase.findById(TodoId(id))
            ?.let { it.toApi() }
            ?: throw NotFoundException()

    @GetMapping("/todos")
    suspend fun getAll(
        @ParameterObject parameters: TodoQueryParameters,
    ): List<TodoResponse> =
        findTodoUseCase.findAll(parameters.incompleteOnly)
            .map { it.toApi() }

    @PostMapping("/todos")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createTodo(
        @RequestBody request: CreateTodoRequest,
    ): TodoResponse {
        val command = request.toCommand()
        val todo: Todo = createTodoUseCase.create(command)
        return todo.toApi()
    }
}

fun Todo.toApi(): TodoResponse {
    return TodoResponse(
        id = this.id.value,
        title = this.title,
        description = this.description,
        completed = this.completed,
    )
}

fun CreateTodoRequest.toCommand(): SaveTodoCommand {
    return SaveTodoCommand(
        title = this.title,
        description = this.description,
        completed = false,
    )
}
