package com.dox.domain.billing

import java.time.LocalDateTime
import java.util.UUID

data class TenantPromotion(
    val id: UUID = UUID.randomUUID(),
    val tenantId: UUID,
    val promotionId: UUID,
    val appliedAt: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime? = null,
    val appliedByUserId: UUID? = null,
    val sourceEvent: String? = null,
    val status: TenantPromotionStatus = TenantPromotionStatus.ACTIVE,
    val notes: String? = null,
)
