package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.ModulePriceJpaEntity
import com.dox.adapter.out.persistence.repository.ModulePriceJpaRepository
import com.dox.application.port.output.ModulePricePersistencePort
import com.dox.domain.billing.ModulePrice
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Component
class ModulePricePersistenceAdapter(
    private val repository: ModulePriceJpaRepository,
) : ModulePricePersistencePort {
    override fun findCurrentPrice(moduleId: String): ModulePrice? = repository.findFirstByModuleIdAndValidUntilIsNull(moduleId)?.toDomain()

    override fun findHistory(
        moduleId: String,
        limit: Int,
    ): List<ModulePrice> =
        repository.findByModuleIdOrderByValidFromDesc(moduleId, PageRequest.of(0, limit))
            .map { it.toDomain() }

    override fun save(modulePrice: ModulePrice): ModulePrice {
        val entity =
            ModulePriceJpaEntity(
                id = modulePrice.id ?: UUID.randomUUID(),
                moduleId = modulePrice.moduleId,
                priceCents = modulePrice.priceCents,
                currency = modulePrice.currency,
                validFrom = modulePrice.validFrom,
                validUntil = modulePrice.validUntil,
                notes = modulePrice.notes,
                createdByUserId = modulePrice.createdByUserId,
            )
        return repository.save(entity).toDomain()
    }

    @Transactional
    override fun expireCurrent(
        moduleId: String,
        validUntil: LocalDateTime,
    ): Int = repository.expireCurrent(moduleId, validUntil)

    private fun ModulePriceJpaEntity.toDomain() =
        ModulePrice(
            id = id,
            moduleId = moduleId,
            priceCents = priceCents,
            currency = currency,
            validFrom = validFrom,
            validUntil = validUntil,
            notes = notes,
            createdByUserId = createdByUserId,
            createdAt = createdAt,
        )
}
