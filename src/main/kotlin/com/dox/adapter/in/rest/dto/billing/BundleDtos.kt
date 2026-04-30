package com.dox.adapter.`in`.rest.dto.billing

data class BundleResponse(
    val id: String,
    val name: String,
    val modules: List<BundleModuleResponse>,
    val priceMonthlyCents: Int,
    val priceYearlyCents: Int,
    val highlighted: Boolean,
)

data class BundleModuleResponse(
    val id: String,
    val displayName: String,
)
