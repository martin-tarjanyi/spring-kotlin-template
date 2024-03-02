package com.example.product.web.controller

import com.example.product.domain.model.SaveTodoCommand
import com.example.product.domain.model.Todo
import com.example.product.domain.port.inward.CreateTodoUseCase
import com.example.product.domain.port.inward.FindTodoUseCase
import com.example.product.web.error.NotFoundException
import com.example.product.web.model.request.CreateTodoRequest
import com.example.product.web.model.request.TodoQueryParameters
import com.example.product.web.model.response.TodoResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers
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
    private val mapper = Mappers.getMapper(TodoApiMapper::class.java)

    @GetMapping("/todos/{id}")
    suspend fun getTodo(
        @PathVariable("id") id: String,
    ): TodoResponse =
        findTodoUseCase.findById(id)
            ?.let { mapper.toApi(it) }
            ?: throw NotFoundException()

    @GetMapping("/todos")
    suspend fun getAll(
        @ParameterObject parameters: TodoQueryParameters,
    ): List<TodoResponse> =
        findTodoUseCase.findAll(parameters.incompleteOnly)
            .map { mapper.toApi(it) }

    @PostMapping("/todos")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createTodo(
        @RequestBody request: CreateTodoRequest,
    ): TodoResponse {
        val command = mapper.toCommand(request)
        val todo: Todo = createTodoUseCase.create(command)
        return mapper.toApi(todo)
    }
}

@Mapper
internal interface TodoApiMapper {
    fun toApi(todo: Todo): TodoResponse

    @Mapping(target = "completed", constant = "false")
    fun toCommand(request: CreateTodoRequest): SaveTodoCommand
}
