package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.BundlePriceJpaEntity
import com.dox.adapter.out.persistence.repository.BundlePriceJpaRepository
import com.dox.application.port.output.BundlePricePersistencePort
import com.dox.domain.billing.BundlePrice
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Component
class BundlePricePersistenceAdapter(
    private val repository: BundlePriceJpaRepository,
) : BundlePricePersistencePort {
    override fun findCurrent(bundleId: String): BundlePrice? = repository.findFirstByBundleIdAndValidUntilIsNull(bundleId)?.toDomain()

    override fun findById(id: UUID): BundlePrice? = repository.findById(id).orElse(null)?.toDomain()

    override fun history(
        bundleId: String,
        limit: Int,
    ): List<BundlePrice> =
        repository.findByBundleIdOrderByValidFromDesc(bundleId, PageRequest.of(0, limit))
            .map { it.toDomain() }

    override fun save(price: BundlePrice): BundlePrice {
        val entity =
            BundlePriceJpaEntity(
                id = price.id ?: UUID.randomUUID(),
                bundleId = price.bundleId,
                priceMonthlyCents = price.priceMonthlyCents,
                priceYearlyCents = price.priceYearlyCents,
                seatsIncluded = price.seatsIncluded,
                trackingSlotsIncluded = price.trackingSlotsIncluded,
                validFrom = price.validFrom,
                validUntil = price.validUntil,
                notes = price.notes,
                createdByUserId = price.createdByUserId,
            )
        return repository.save(entity).toDomain()
    }

    @Transactional
    override fun expireCurrent(bundleId: String) {
        repository.expireCurrent(bundleId, LocalDateTime.now())
    }

    private fun BundlePriceJpaEntity.toDomain() =
        BundlePrice(
            id = id,
            bundleId = bundleId,
            priceMonthlyCents = priceMonthlyCents,
            priceYearlyCents = priceYearlyCents,
            seatsIncluded = seatsIncluded,
            trackingSlotsIncluded = trackingSlotsIncluded,
            validFrom = validFrom,
            validUntil = validUntil,
            notes = notes,
            createdByUserId = createdByUserId,
            createdAt = createdAt,
        )
}
