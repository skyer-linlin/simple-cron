package com.lin.simplecron

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootApplication
@EnableAspectJAutoProxy
class SimplecronApplication

fun main(args: Array<String>) {
    runApplication<SimplecronApplication>(*args)
}
