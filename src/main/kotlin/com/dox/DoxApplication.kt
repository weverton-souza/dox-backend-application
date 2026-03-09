package com.dox

import com.dox.config.SecurityProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(SecurityProperties::class)
class DoxApplication

fun main(args: Array<String>) {
    runApplication<DoxApplication>(*args)
}
