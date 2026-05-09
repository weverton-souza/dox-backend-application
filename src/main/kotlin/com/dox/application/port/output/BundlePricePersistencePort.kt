package com.dox.application.port.output

import com.dox.domain.billing.BundlePrice
import java.util.UUID

interface BundlePricePersistencePort {
    fun findCurrent(bundleId: String): BundlePrice?

    fun findById(id: UUID): BundlePrice?

    fun history(
        bundleId: String,
        limit: Int,
    ): List<BundlePrice>

    fun save(price: BundlePrice): BundlePrice

    fun expireCurrent(bundleId: String)
}
