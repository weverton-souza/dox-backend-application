package com.dox.domain.billing

import java.time.LocalDateTime
import java.util.UUID

data class ModulePrice(
    val id: UUID? = null,
    val moduleId: String,
    val priceCents: Int,
    val currency: String = "BRL",
    val validFrom: LocalDateTime,
    val validUntil: LocalDateTime? = null,
    val notes: String? = null,
    val createdByUserId: UUID? = null,
    val createdAt: LocalDateTime? = null,
)
