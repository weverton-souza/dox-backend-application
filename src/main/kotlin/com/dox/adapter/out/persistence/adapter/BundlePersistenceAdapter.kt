package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.BundleJpaEntity
import com.dox.adapter.out.persistence.repository.BundleJpaRepository
import com.dox.application.port.output.BundlePersistencePort
import com.dox.domain.billing.Bundle
import org.springframework.stereotype.Component

@Component
class BundlePersistenceAdapter(
    private val repository: BundleJpaRepository,
) : BundlePersistencePort {
    override fun findAllActive(): List<Bundle> = repository.findAllByActiveTrue().map { it.toDomain() }

    override fun findById(id: String): Bundle? = repository.findById(id).orElse(null)?.toDomain()

    private fun BundleJpaEntity.toDomain() =
        Bundle(
            id = id,
            name = name,
            modules = modules,
            priceMonthlyCents = priceMonthlyCents,
            priceYearlyCents = priceYearlyCents,
            highlighted = highlighted,
            active = active,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
}
