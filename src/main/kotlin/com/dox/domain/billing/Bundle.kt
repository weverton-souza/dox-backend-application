package com.dox.domain.billing

import java.time.LocalDateTime

data class Bundle(
    val id: String,
    val name: String,
    val modules: List<String>,
    val priceMonthlyCents: Int,
    val priceYearlyCents: Int,
    val highlighted: Boolean = false,
    val active: Boolean = true,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
