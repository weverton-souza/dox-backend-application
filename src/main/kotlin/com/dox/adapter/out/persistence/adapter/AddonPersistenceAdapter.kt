package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.AddonJpaEntity
import com.dox.adapter.out.persistence.repository.AddonJpaRepository
import com.dox.application.port.output.AddonPersistencePort
import com.dox.domain.billing.Addon
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
class AddonPersistenceAdapter(
    private val repository: AddonJpaRepository,
) : AddonPersistencePort {
    override fun findAll(): List<Addon> = repository.findAll(Sort.by(Sort.Direction.ASC, "sortOrder")).map { it.toDomain() }

    override fun findById(id: String): Addon? = repository.findById(id).orElse(null)?.toDomain()

    override fun save(addon: Addon): Addon {
        val entity =
            repository.findById(addon.id).orElseGet {
                AddonJpaEntity(
                    id = addon.id,
                    name = addon.name,
                    type = addon.type,
                )
            }
        entity.name = addon.name
        entity.description = addon.description
        entity.type = addon.type
        entity.targetModuleId = addon.targetModuleId
        entity.priceMonthlyCents = addon.priceMonthlyCents
        entity.priceUnitCents = addon.priceUnitCents
        entity.feePercentage = addon.feePercentage
        entity.availableForBundles = addon.availableForBundles
        entity.active = addon.active
        entity.sortOrder = addon.sortOrder
        return repository.save(entity).toDomain()
    }

    private fun AddonJpaEntity.toDomain() =
        Addon(
            id = id,
            name = name,
            description = description,
            type = type,
            targetModuleId = targetModuleId,
            priceMonthlyCents = priceMonthlyCents,
            priceUnitCents = priceUnitCents,
            feePercentage = feePercentage,
            availableForBundles = availableForBundles,
            active = active,
            sortOrder = sortOrder,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
}
