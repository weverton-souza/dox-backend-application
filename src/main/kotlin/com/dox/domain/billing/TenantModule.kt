package com.dox.domain.billing

import java.time.LocalDateTime
import java.util.UUID

data class TenantModule(
    val id: UUID = UUID.randomUUID(),
    val tenantId: UUID,
    val moduleId: String,
    val status: ModuleStatus,
    val source: ModuleSource,
    val sourceId: String? = null,
    val activatedAt: LocalDateTime,
    val expiresAt: LocalDateTime? = null,
    val graceUntil: LocalDateTime? = null,
    val basePriceCents: Int = 0,
    val finalPriceCents: Int = 0,
    val priceLocked: Boolean = true,
    val priceLockedAt: LocalDateTime? = null,
    val canceledAt: LocalDateTime? = null,
    val cancelReason: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
