package com.dox.adapter.`in`.rest.dto.billing

data class BundleResponse(
    val id: String,
    val name: String,
    val description: String?,
    val modules: List<BundleModuleResponse>,
    val priceMonthlyCents: Int,
    val priceYearlyCents: Int,
    val seatsIncluded: Int,
    val trackingSlotsIncluded: Int,
    val highlighted: Boolean,
    val sortOrder: Int,
)

data class BundleModuleResponse(
    val id: String,
    val displayName: String,
)
