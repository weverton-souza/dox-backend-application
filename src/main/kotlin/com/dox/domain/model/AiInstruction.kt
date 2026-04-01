package com.dox.domain.model

import com.dox.domain.enum.Vertical
import java.time.LocalDateTime
import java.util.UUID

data class AiInstruction(
    val id: UUID = UUID.randomUUID(),
    val type: String,
    val vertical: Vertical? = null,
    val content: String,
    val active: Boolean = true,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
