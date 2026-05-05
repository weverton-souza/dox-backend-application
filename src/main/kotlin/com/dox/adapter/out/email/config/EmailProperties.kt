package com.dox.adapter.out.email.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "dox.email")
data class EmailProperties(
    val enabled: Boolean = false,
    val provider: String = "resend",
    val apiKey: String = "",
    val fromAddress: String = "DOX <onboarding@resend.dev>",
    val replyTo: String = "",
    val webhookSecret: String = "",
    val apiBaseUrl: String = "http://localhost:8080",
    val landingUrl: String = "http://localhost:3000",
    val frontendUrl: String = "http://localhost:5173",
)
