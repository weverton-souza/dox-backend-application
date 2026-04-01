package com.dox.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class AiGenerationSource(
    val id: UUID = UUID.randomUUID(),
    val reportId: UUID,
    val generationId: UUID,
    val sourceType: String,
    val sourceId: UUID,
    val sourceLabel: String? = null,
    val included: Boolean = true,
    val displayOrder: Int = 0,
    val createdAt: LocalDateTime? = null,
)
