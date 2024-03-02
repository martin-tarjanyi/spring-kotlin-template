package com.example.product.dataaccess.mongo

import com.example.product.domain.model.SaveTodoCommand
import com.example.product.domain.port.out.TodoPersistencePort
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired

internal class TodoMongoPersistenceIntegrationTest : BaseMongoIntegrationTest() {
    @Autowired
    private lateinit var persistencePort: TodoPersistencePort

    init {
        context("find by ID") {
            should("return the todo") {
                // given
                val todo = persistencePort.save(createTodoForSave())

                // when-then
                persistencePort.findById(todo.id)
                    .shouldNotBeNull()
                    .let {
                        it.title shouldBe "title"
                        it.description shouldBe "description"
                        it.completed shouldBe false
                    }
            }

            should("return null if the todo does not exist") {
                // when-then
                persistencePort.findById(ObjectId().toString())
                    .shouldBe(null)
            }
        }

        context("find all") {
            should("return all todos") {
                // given
                persistencePort.save(createTodoForSave(completed = true))
                persistencePort.save(createTodoForSave(completed = false))

                // when-then
                persistencePort.findAll(incompleteOnly = false)
                    .shouldHaveSize(2)
            }

            should("return only incomplete todos") {
                // given
                persistencePort.save(createTodoForSave(completed = false))
                persistencePort.save(createTodoForSave(completed = true))

                // when-then
                persistencePort.findAll(incompleteOnly = true)
                    .shouldHaveSize(1)
                    .forEach { it.completed shouldBe false }
            }
        }
    }

    private fun createTodoForSave(
        title: String = "title",
        description: String = "description",
        completed: Boolean = false,
    ) = SaveTodoCommand(
        title = title,
        description = description,
        completed = completed,
    )
}
