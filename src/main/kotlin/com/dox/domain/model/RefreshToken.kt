package com.dox.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class RefreshToken(
    val id: UUID = UUID.randomUUID(),
    val userId: UUID,
    val tokenHash: String,
    val expiresAt: LocalDateTime,
    val createdAt: LocalDateTime? = null,
)
