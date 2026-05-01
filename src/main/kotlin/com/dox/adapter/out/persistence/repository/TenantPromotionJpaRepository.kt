package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.TenantPromotionJpaEntity
import com.dox.domain.billing.TenantPromotionStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TenantPromotionJpaRepository : JpaRepository<TenantPromotionJpaEntity, UUID> {
    fun findByTenantIdAndStatus(
        tenantId: UUID,
        status: TenantPromotionStatus,
    ): List<TenantPromotionJpaEntity>

    fun findByTenantIdAndPromotionId(
        tenantId: UUID,
        promotionId: UUID,
    ): TenantPromotionJpaEntity?
}
