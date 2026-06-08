package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.TenantAddonJpaEntity
import com.dox.adapter.out.persistence.repository.TenantAddonJpaRepository
import com.dox.application.port.output.TenantAddonPersistencePort
import com.dox.domain.billing.TenantAddon
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class TenantAddonPersistenceAdapter(
    private val repository: TenantAddonJpaRepository,
) : TenantAddonPersistencePort {
    override fun activeQuantity(
        tenantId: UUID,
        addonId: String,
    ): Int = repository.findByTenantIdAndAddonIdAndCanceledAtIsNull(tenantId, addonId)?.quantity ?: 0

    override fun findByTenantAndAddon(
        tenantId: UUID,
        addonId: String,
    ): TenantAddon? = repository.findByTenantIdAndAddonId(tenantId, addonId)?.toDomain()

    override fun save(tenantAddon: TenantAddon): TenantAddon {
        val entity = tenantAddon.id?.let { repository.findById(it).orElse(null) } ?: TenantAddonJpaEntity()
        entity.tenantId = tenantAddon.tenantId
        entity.addonId = tenantAddon.addonId
        entity.quantity = tenantAddon.quantity
        entity.activatedAt = tenantAddon.activatedAt
        entity.canceledAt = tenantAddon.canceledAt
        entity.basePriceCents = tenantAddon.basePriceCents
        entity.finalPriceCents = tenantAddon.finalPriceCents
        return repository.save(entity).toDomain()
    }

    private fun TenantAddonJpaEntity.toDomain() =
        TenantAddon(
            id = id,
            tenantId = tenantId,
            addonId = addonId,
            quantity = quantity,
            activatedAt = activatedAt,
            canceledAt = canceledAt,
            basePriceCents = basePriceCents,
            finalPriceCents = finalPriceCents,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
}
