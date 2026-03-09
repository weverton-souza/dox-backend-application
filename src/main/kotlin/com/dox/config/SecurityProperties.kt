package com.dox.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "dox.security")
data class SecurityProperties(
    val jwtSigningKey: String = "change-me-in-production",
    val accessTokenExpiration: Long = 900_000,
    val refreshTokenExpiration: Long = 604_800_000
)
