package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.BundleJpaEntity
import com.dox.adapter.out.persistence.repository.BundleJpaRepository
import com.dox.application.port.output.BundlePersistencePort
import com.dox.domain.billing.Bundle
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
class BundlePersistenceAdapter(
    private val repository: BundleJpaRepository,
) : BundlePersistencePort {
    override fun findAllActive(): List<Bundle> = repository.findAllByActiveTrue().map { it.toDomain() }

    override fun findAll(): List<Bundle> = repository.findAll(Sort.by(Sort.Direction.ASC, "sortOrder")).map { it.toDomain() }

    override fun findById(id: String): Bundle? = repository.findById(id).orElse(null)?.toDomain()

    override fun save(bundle: Bundle): Bundle {
        val entity =
            repository.findById(bundle.id).orElseGet {
                BundleJpaEntity(
                    id = bundle.id,
                    name = bundle.name,
                    modules = bundle.modules,
                )
            }
        entity.name = bundle.name
        entity.description = bundle.description
        entity.modules = bundle.modules
        entity.priceMonthlyCents = bundle.priceMonthlyCents
        entity.priceYearlyCents = bundle.priceYearlyCents
        entity.seatsIncluded = bundle.seatsIncluded
        entity.trackingSlotsIncluded = bundle.trackingSlotsIncluded
        entity.highlighted = bundle.highlighted
        entity.active = bundle.active
        entity.sortOrder = bundle.sortOrder
        return repository.save(entity).toDomain()
    }

    private fun BundleJpaEntity.toDomain() =
        Bundle(
            id = id,
            name = name,
            description = description,
            modules = modules,
            priceMonthlyCents = priceMonthlyCents,
            priceYearlyCents = priceYearlyCents,
            seatsIncluded = seatsIncluded,
            trackingSlotsIncluded = trackingSlotsIncluded,
            highlighted = highlighted,
            active = active,
            sortOrder = sortOrder,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
}
