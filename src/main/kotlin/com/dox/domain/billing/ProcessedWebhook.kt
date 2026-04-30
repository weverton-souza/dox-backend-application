package com.dox.domain.billing

import java.time.LocalDateTime
import java.util.UUID

data class ProcessedWebhook(
    val id: UUID = UUID.randomUUID(),
    val asaasEventId: String,
    val eventType: String,
    val payload: Map<String, Any?>,
    val processedAt: LocalDateTime,
)
