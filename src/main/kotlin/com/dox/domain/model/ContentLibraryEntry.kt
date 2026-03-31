package com.dox.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class ContentLibraryEntry(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val content: List<Map<String, Any?>>,
    val type: String = "reference",
    val category: String = "general",
    val instrument: String? = null,
    val authors: String? = null,
    val year: Int? = null,
    val tags: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)
