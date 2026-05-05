package com.dox.domain.email

import java.time.LocalDateTime
import java.util.UUID

enum class EmailSuppressionReason {
    HARD_BOUNCE,
    COMPLAINT,
    MANUAL,
    INVALID,
}

data class EmailSuppression(
    val id: UUID = UUID.randomUUID(),
    val email: String,
    val reason: EmailSuppressionReason,
    val notes: String? = null,
    val suppressedAt: LocalDateTime = LocalDateTime.now(),
)
