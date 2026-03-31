package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.AiQuotaJpaEntity
import com.dox.adapter.out.persistence.repository.AiQuotaJpaRepository
import com.dox.application.port.output.AiQuotaPort
import com.dox.domain.model.AiQuota
import org.springframework.stereotype.Component

@Component
class AiQuotaPersistenceAdapter(
    private val aiQuotaJpaRepository: AiQuotaJpaRepository
) : AiQuotaPort {
    override fun findQuota(): AiQuota? =
        aiQuotaJpaRepository.findFirstByOrderByCreatedAtAsc()?.toDomain()

    override fun save(quota: AiQuota): AiQuota {
        val entity = aiQuotaJpaRepository.findById(quota.id).orElse(null)
            ?: AiQuotaJpaEntity().apply { id = quota.id }
        entity.aiTier = quota.aiTier
        entity.model = quota.model
        entity.monthlyLimit = quota.monthlyLimit
        entity.overagePriceCents = quota.overagePriceCents
        entity.enabled = quota.enabled
        return aiQuotaJpaRepository.save(entity).toDomain()
    }

    private fun AiQuotaJpaEntity.toDomain() = AiQuota(
        id = id,
        aiTier = aiTier,
        model = model,
        monthlyLimit = monthlyLimit,
        overagePriceCents = overagePriceCents,
        enabled = enabled,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
