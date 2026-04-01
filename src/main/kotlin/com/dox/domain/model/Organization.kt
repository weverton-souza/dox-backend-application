package com.dox.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class Organization(
    val id: UUID = UUID.randomUUID(),
    val tenantId: UUID,
    val name: String,
    val description: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
