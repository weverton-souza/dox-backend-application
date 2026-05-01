package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.TenantPromotionJpaEntity
import com.dox.adapter.out.persistence.repository.TenantPromotionJpaRepository
import com.dox.application.port.output.TenantPromotionPersistencePort
import com.dox.domain.billing.TenantPromotion
import com.dox.domain.billing.TenantPromotionStatus
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Component
class TenantPromotionPersistenceAdapter(
    private val repository: TenantPromotionJpaRepository,
) : TenantPromotionPersistencePort {
    override fun findActiveByTenantId(tenantId: UUID): List<TenantPromotion> = repository.findByTenantIdAndStatus(tenantId, TenantPromotionStatus.ACTIVE).map { it.toDomain() }

    override fun findById(id: UUID): TenantPromotion? = repository.findById(id).orElse(null)?.toDomain()

    override fun findByTenantIdAndPromotionId(
        tenantId: UUID,
        promotionId: UUID,
    ): TenantPromotion? = repository.findByTenantIdAndPromotionId(tenantId, promotionId)?.toDomain()

    override fun save(tenantPromotion: TenantPromotion): TenantPromotion {
        val entity =
            repository.findById(tenantPromotion.id).orElseGet {
                TenantPromotionJpaEntity(
                    id = tenantPromotion.id,
                    tenantId = tenantPromotion.tenantId,
                    promotionId = tenantPromotion.promotionId,
                )
            }
        entity.tenantId = tenantPromotion.tenantId
        entity.promotionId = tenantPromotion.promotionId
        entity.appliedAt = tenantPromotion.appliedAt
        entity.expiresAt = tenantPromotion.expiresAt
        entity.appliedByUserId = tenantPromotion.appliedByUserId
        entity.sourceEvent = tenantPromotion.sourceEvent
        entity.status = tenantPromotion.status
        entity.notes = tenantPromotion.notes
        return repository.save(entity).toDomain()
    }

    @Transactional
    override fun markExpiredOlderThan(now: LocalDateTime): Int = repository.markExpiredOlderThan(now)

    private fun TenantPromotionJpaEntity.toDomain() =
        TenantPromotion(
            id = id,
            tenantId = tenantId,
            promotionId = promotionId,
            appliedAt = appliedAt,
            expiresAt = expiresAt,
            appliedByUserId = appliedByUserId,
            sourceEvent = sourceEvent,
            status = status,
            notes = notes,
        )
}
