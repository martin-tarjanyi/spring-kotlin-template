package com.example.product

import com.example.product.domain.model.Todo
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TodoWebApplicationSpec : ShouldSpec() {
    override fun extensions() = listOf(SpringExtension)

    init {
        should("context loads") {
            Todo("1")
        }

        should("context loads 2") {
            Todo("2")
        }
    }
}
