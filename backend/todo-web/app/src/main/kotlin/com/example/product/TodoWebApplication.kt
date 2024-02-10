package com.example.product

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TodoWebApplication

fun main(args: Array<String>) {
	runApplication<TodoWebApplication>(*args)
}
