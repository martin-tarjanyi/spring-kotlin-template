package com.example.product

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(
    exclude = [MongoReactiveAutoConfiguration::class, RedisReactiveAutoConfiguration::class, RedisAutoConfiguration::class],
)
class TodoWebApplication

fun main(args: Array<String>) {
    runApplication<TodoWebApplication>(*args)
}
