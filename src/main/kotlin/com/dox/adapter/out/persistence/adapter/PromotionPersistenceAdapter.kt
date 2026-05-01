package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.PromotionJpaEntity
import com.dox.adapter.out.persistence.repository.PromotionJpaRepository
import com.dox.application.port.output.PromotionPersistencePort
import com.dox.domain.billing.Promotion
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Component
class PromotionPersistenceAdapter(
    private val repository: PromotionJpaRepository,
) : PromotionPersistencePort {
    override fun findById(id: UUID): Promotion? = repository.findById(id).orElse(null)?.toDomain()

    override fun findByCode(code: String): Promotion? = repository.findByCode(code)?.toDomain()

    override fun save(promotion: Promotion): Promotion {
        val entity =
            repository.findById(promotion.id).orElseGet {
                PromotionJpaEntity(id = promotion.id, name = promotion.name)
            }
        entity.code = promotion.code
        entity.name = promotion.name
        entity.type = promotion.type
        entity.discountType = promotion.discountType
        entity.discountValue = promotion.discountValue
        entity.durationType = promotion.durationType
        entity.durationMonths = promotion.durationMonths
        entity.maxRedemptions = promotion.maxRedemptions
        entity.currentRedemptions = promotion.currentRedemptions
        entity.validFrom = promotion.validFrom
        entity.validUntil = promotion.validUntil
        entity.appliesTo = promotion.appliesTo
        entity.appliesToModules = promotion.appliesToModules.takeIf { it.isNotEmpty() }
        entity.appliesToVerticals = promotion.appliesToVerticals.takeIf { it.isNotEmpty() }
        entity.appliesToSignupAfter = promotion.appliesToSignupAfter
        entity.appliesToSignupBefore = promotion.appliesToSignupBefore
        entity.stackableWith = promotion.stackableWith
        entity.skipProration = promotion.skipProration
        entity.requiresApproval = promotion.requiresApproval
        entity.autoApplyEvent = promotion.autoApplyEvent
        entity.partnerId = promotion.partnerId
        entity.nextPromotionId = promotion.nextPromotionId
        entity.createdByUserId = promotion.createdByUserId
        entity.archivedAt = promotion.archivedAt
        return repository.save(entity).toDomain()
    }

    override fun listAll(includeArchived: Boolean): List<Promotion> {
        val entities =
            if (includeArchived) repository.findAll() else repository.findAllByArchivedAtIsNull()
        return entities.map { it.toDomain() }
    }

    override fun findPaginated(
        includeArchived: Boolean,
        pageable: Pageable,
    ): Page<Promotion> {
        val page =
            if (includeArchived) repository.findAll(pageable) else repository.findAllByArchivedAtIsNull(pageable)
        return page.map { it.toDomain() }
    }

    @Transactional
    override fun tryIncrementRedemption(
        promotionId: UUID,
        now: LocalDateTime,
    ): Boolean = repository.tryIncrementRedemption(promotionId, now) == 1

    private fun PromotionJpaEntity.toDomain() =
        Promotion(
            id = id,
            code = code,
            name = name,
            type = type,
            discountType = discountType,
            discountValue = discountValue,
            durationType = durationType,
            durationMonths = durationMonths,
            maxRedemptions = maxRedemptions,
            currentRedemptions = currentRedemptions,
            validFrom = validFrom,
            validUntil = validUntil,
            appliesTo = appliesTo,
            appliesToModules = appliesToModules ?: emptyList(),
            appliesToVerticals = appliesToVerticals ?: emptyList(),
            appliesToSignupAfter = appliesToSignupAfter,
            appliesToSignupBefore = appliesToSignupBefore,
            stackableWith = stackableWith,
            skipProration = skipProration,
            requiresApproval = requiresApproval,
            autoApplyEvent = autoApplyEvent,
            partnerId = partnerId,
            nextPromotionId = nextPromotionId,
            createdAt = createdAt,
            createdByUserId = createdByUserId,
            archivedAt = archivedAt,
        )
}
