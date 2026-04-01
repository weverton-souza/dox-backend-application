package com.dox.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class ProfessionalSettings(
    val id: UUID = UUID.randomUUID(),
    val name: String = "",
    val crp: String? = null,
    val specialization: String = "",
    val phone: String? = null,
    val instagram: String? = null,
    val email: String? = null,
    val logo: String? = null,
    val contactItems: List<Map<String, Any?>> = emptyList(),
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
