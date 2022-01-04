package com.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class TaskGraphqlApplication

fun main(args: Array<String>) {
    runApplication<TaskGraphqlApplication>(*args)
}
