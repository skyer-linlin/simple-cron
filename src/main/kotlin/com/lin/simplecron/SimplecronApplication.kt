package com.lin.simplecron

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.retry.annotation.EnableRetry
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableScheduling
@EnableRetry //重试注解
class SimplecronApplication

fun main(args: Array<String>) {
    runApplication<SimplecronApplication>(*args)
}
