package com.dox.domain.billing

data class PriceBreakdown(
    val basePriceCents: Int,
    val bundleDiscountCents: Int = 0,
    val finalPriceCents: Int,
    val cycle: BillingCycle = BillingCycle.MONTHLY,
)
