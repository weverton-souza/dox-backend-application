package com.dox.extensions

import jakarta.servlet.http.HttpServletRequest

private const val MAX_IP_LENGTH = 45

fun HttpServletRequest.extractClientIp(): String? {
    val forwarded = getHeader("X-Forwarded-For")
    if (!forwarded.isNullOrBlank()) {
        return forwarded.split(",").first().trim().take(MAX_IP_LENGTH)
    }
    return remoteAddr?.take(MAX_IP_LENGTH)
}
