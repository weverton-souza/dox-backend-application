package com.dox.domain.billing

import java.time.LocalDateTime
import java.util.UUID

data class TenantAddon(
    val id: UUID? = null,
    val tenantId: UUID,
    val addonId: String,
    val quantity: Int = 1,
    val activatedAt: LocalDateTime,
    val canceledAt: LocalDateTime? = null,
    val basePriceCents: Int,
    val finalPriceCents: Int,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
