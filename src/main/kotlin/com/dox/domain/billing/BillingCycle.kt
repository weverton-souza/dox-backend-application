package com.dox.domain.billing

enum class BillingCycle(
    val months: Int,
    val asaasValue: String,
) {
    MONTHLY(1, "MONTHLY"),
    QUARTERLY(3, "QUARTERLY"),
    SEMIANNUALLY(6, "SEMIANNUALLY"),
    YEARLY(12, "YEARLY"),
}
