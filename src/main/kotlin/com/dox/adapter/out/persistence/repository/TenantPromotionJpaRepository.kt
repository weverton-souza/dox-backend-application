package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.TenantPromotionJpaEntity
import com.dox.domain.billing.TenantPromotionStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
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

    @Modifying
    @Query(
        "UPDATE TenantPromotionJpaEntity tp SET tp.status = com.dox.domain.billing.TenantPromotionStatus.EXPIRED " +
            "WHERE tp.status = com.dox.domain.billing.TenantPromotionStatus.ACTIVE " +
            "AND tp.expiresAt IS NOT NULL " +
            "AND tp.expiresAt < :now",
    )
    fun markExpiredOlderThan(
        @Param("now") now: LocalDateTime,
    ): Int
}
