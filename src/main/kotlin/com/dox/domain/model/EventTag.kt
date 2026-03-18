package com.dox.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class EventTag(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val color: String,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)
