package com.dox.adapter.`in`.rest.dto.admin

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.UUID

data class AdminModuleListItem(
    val moduleId: String,
    val displayName: String,
    val dependencies: List<String>,
    val basePriceCents: Int,
    val currentPriceCents: Int?,
    val currentValidFrom: LocalDateTime?,
    val hasFallback: Boolean,
)

data class AdminModulePriceResponse(
    val id: UUID,
    val moduleId: String,
    val priceCents: Int,
    val currency: String,
    val validFrom: LocalDateTime,
    val validUntil: LocalDateTime?,
    val notes: String?,
    val createdByUserId: UUID?,
    val createdAt: LocalDateTime?,
)

data class UpdateModulePriceRequest(
    @field:Min(value = 0, message = "Preço deve ser maior ou igual a zero")
    val priceCents: Int,
    @field:Size(max = 500, message = "Notas devem ter no máximo 500 caracteres")
    val notes: String? = null,
)

data class AdminBundleListItem(
    val id: String,
    val name: String,
    val description: String?,
    val modules: List<String>,
    val priceMonthlyCents: Int,
    val priceYearlyCents: Int,
    val seatsIncluded: Int,
    val trackingSlotsIncluded: Int,
    val highlighted: Boolean,
    val active: Boolean,
    val sortOrder: Int,
    val updatedAt: LocalDateTime?,
)

data class UpdateBundleRequest(
    @field:Min(value = 0, message = "Preço mensal deve ser maior ou igual a zero")
    val priceMonthlyCents: Int? = null,
    @field:Min(value = 0, message = "Preço anual deve ser maior ou igual a zero")
    val priceYearlyCents: Int? = null,
    @field:Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    val description: String? = null,
    @field:Min(value = 1, message = "Vagas inclusas devem ser pelo menos 1")
    val seatsIncluded: Int? = null,
    @field:Min(value = 0, message = "Slots de tracking devem ser maior ou igual a zero")
    val trackingSlotsIncluded: Int? = null,
    val highlighted: Boolean? = null,
    val sortOrder: Int? = null,
    @field:Size(max = 500, message = "Notas devem ter no máximo 500 caracteres")
    val notes: String? = null,
)
