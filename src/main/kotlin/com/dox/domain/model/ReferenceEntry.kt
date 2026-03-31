package com.dox.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class ReferenceEntry(
    val id: UUID = UUID.randomUUID(),
    val text: String,
    val instrument: String? = null,
    val authors: String? = null,
    val year: Int? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)
