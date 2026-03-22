package com.dox.config

import jakarta.annotation.PostConstruct
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "dox.security")
data class SecurityProperties(
    val jwtSigningKey: String,
    val accessTokenExpiration: Long = 900_000,
    val refreshTokenExpiration: Long = 604_800_000
) {
    @PostConstruct
    fun validate() {
        require(jwtSigningKey.length >= 32) {
            "JWT signing key must be at least 32 characters. Set the JWT_SECRET environment variable."
        }
        require(jwtSigningKey != "change-me-in-production-this-is-a-long-enough-key-for-hmac-sha256-algorithm") {
            "JWT signing key must not be the default value. Set a secure JWT_SECRET environment variable."
        }
    }
}
