package com.dox.adapter.`in`.rest.dto.billing

import com.dox.domain.billing.AddonType
import java.math.BigDecimal

data class AddonResponse(
    val id: String,
    val name: String,
    val description: String?,
    val type: AddonType,
    val targetModuleId: String?,
    val priceMonthlyCents: Int,
    val priceUnitCents: Int?,
    val feePercentage: BigDecimal?,
    val availableForBundles: List<String>,
    val sortOrder: Int,
)
