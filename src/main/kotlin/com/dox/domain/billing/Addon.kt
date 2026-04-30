package com.dox.domain.billing

import java.math.BigDecimal
import java.time.LocalDateTime

data class Addon(
    val id: String,
    val name: String,
    val description: String? = null,
    val type: AddonType,
    val targetModuleId: String? = null,
    val priceMonthlyCents: Int = 0,
    val priceUnitCents: Int? = null,
    val feePercentage: BigDecimal? = null,
    val availableForBundles: List<String> = emptyList(),
    val active: Boolean = true,
    val sortOrder: Int = 0,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
