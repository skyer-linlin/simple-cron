package com.lin.simplecron

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SimplecronApplication

fun main(args: Array<String>) {
    runApplication<SimplecronApplication>(*args)
}
