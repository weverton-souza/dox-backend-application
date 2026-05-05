package com.dox

import com.dox.config.SecurityProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableConfigurationProperties(SecurityProperties::class)
@EnableScheduling
@EnableFeignClients
class DoxApplication

fun main(args: Array<String>) {
    runApplication<DoxApplication>(*args)
}
