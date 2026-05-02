package com.dox.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class FormDraft(
    val id: UUID = UUID.randomUUID(),
    val formLinkId: UUID,
    val partialResponse: Map<String, Any?> = emptyMap(),
    val savedAt: LocalDateTime? = null,
)
