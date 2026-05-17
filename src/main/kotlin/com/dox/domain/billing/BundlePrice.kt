package com.dox.domain.billing

import java.time.LocalDateTime
import java.util.UUID

data class BundlePrice(
    val id: UUID? = null,
    val bundleId: String,
    val priceMonthlyCents: Int,
    val priceYearlyCents: Int,
    val seatsIncluded: Int,
    val trackingSlotsIncluded: Int,
    val validFrom: LocalDateTime,
    val validUntil: LocalDateTime? = null,
    val notes: String? = null,
    val createdByUserId: UUID? = null,
    val createdAt: LocalDateTime? = null,
)
