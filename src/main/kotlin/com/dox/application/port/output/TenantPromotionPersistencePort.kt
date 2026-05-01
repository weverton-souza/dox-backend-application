package com.dox.application.port.output

import com.dox.domain.billing.TenantPromotion
import java.time.LocalDateTime
import java.util.UUID

interface TenantPromotionPersistencePort {
    fun findActiveByTenantId(tenantId: UUID): List<TenantPromotion>

    fun findById(id: UUID): TenantPromotion?

    fun findByTenantIdAndPromotionId(
        tenantId: UUID,
        promotionId: UUID,
    ): TenantPromotion?

    fun save(tenantPromotion: TenantPromotion): TenantPromotion

    fun markExpiredOlderThan(now: LocalDateTime): Int
}
