package com.dox.domain.email

import java.time.LocalDateTime
import java.util.UUID

enum class EmailLogStatus {
    PENDING,
    SENT,
    FAILED,
    DELIVERED,
    BOUNCED,
    COMPLAINED,
    OPENED,
    CLICKED,
    SUPPRESSED,
}

data class EmailLog(
    val id: UUID = UUID.randomUUID(),
    val tenantId: UUID? = null,
    val templateId: String,
    val recipientEmail: String,
    val subject: String,
    val providerId: String? = null,
    val status: EmailLogStatus,
    val errorMessage: String? = null,
    val idempotencyKey: String? = null,
    val tags: Map<String, String> = emptyMap(),
    val sentAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)
