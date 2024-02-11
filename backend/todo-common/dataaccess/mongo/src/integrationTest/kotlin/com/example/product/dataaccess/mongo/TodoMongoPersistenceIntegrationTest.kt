package com.example.product.dataaccess.mongo

import com.example.product.domain.model.Todo
import com.example.product.domain.port.out.TodoPersistencePort
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContainExactly
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@SpringBootTest
@EnableAutoConfiguration
class TodoMongoPersistenceIntegrationTest : ShouldSpec() {
    @Autowired
    private lateinit var port: TodoPersistencePort

    init {
        context("findAll") {
            should("return all todos") {
                // given
                port.save(
                    Todo(
                        id = "1",
                        title = "title",
                        description = "description",
                        completed = false,
                    ),
                )
                port.save(
                    Todo(
                        id = "2",
                        title = "title",
                        description = "description",
                        completed = true,
                    ),
                )

                // when-then
                port.findAll(incompleteOnly = false)
                    .map { it.id }
                    .shouldContainExactly("1", "2")
            }

            should("return only incomplete todos") {
                // given
                port.save(
                    Todo(
                        id = "1",
                        title = "title",
                        description = "description",
                        completed = false,
                    ),
                )
                port.save(
                    Todo(
                        id = "2",
                        title = "title",
                        description = "description",
                        completed = true,
                    ),
                )

                // when-then
                port.findAll(incompleteOnly = true)
                    .map { it.id }
                    .shouldContainExactly("1")
            }
        }
    }

    override fun extensions(): List<Extension> = listOf(SpringExtension, MongoKotestExtension)

    @Configuration
    @ComponentScan
    internal class TestConfig
}
