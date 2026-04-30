package com.dox.domain.billing

object BillingCalculator {
    private const val DAYS_IN_BILLING_CYCLE = 30

    fun computeMonthlyBase(modules: Collection<Module>): Int = modules.sumOf { it.basePriceMonthlyCents }

    fun computeForCycle(
        modules: Collection<Module>,
        cycle: BillingCycle,
    ): Int = computeMonthlyBase(modules) * cycle.months

    fun breakdown(
        modules: Collection<Module>,
        cycle: BillingCycle = BillingCycle.MONTHLY,
        bundlePriceCents: Int? = null,
    ): PriceBreakdown {
        val basePriceCents = computeForCycle(modules, cycle)
        val finalPriceCents = bundlePriceCents ?: basePriceCents
        val bundleDiscount = if (bundlePriceCents != null) basePriceCents - bundlePriceCents else 0
        return PriceBreakdown(
            basePriceCents = basePriceCents,
            bundleDiscountCents = bundleDiscount.coerceAtLeast(0),
            finalPriceCents = finalPriceCents.coerceAtLeast(0),
            cycle = cycle,
        )
    }

    fun computeProration(
        monthlyPriceCents: Int,
        daysRemainingInCycle: Int,
    ): Int {
        if (daysRemainingInCycle <= 0) return 0
        val capped = daysRemainingInCycle.coerceAtMost(DAYS_IN_BILLING_CYCLE)
        return (monthlyPriceCents.toLong() * capped / DAYS_IN_BILLING_CYCLE).toInt()
    }
}
