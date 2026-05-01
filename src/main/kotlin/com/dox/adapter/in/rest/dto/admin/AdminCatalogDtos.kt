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
