package com.dox.adapter.`in`.rest.dto.admin

import com.dox.domain.billing.AddonType
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.math.BigDecimal
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

data class AdminAddonListItem(
    val id: String,
    val name: String,
    val description: String?,
    val type: AddonType,
    val targetModuleId: String?,
    val priceMonthlyCents: Int,
    val priceUnitCents: Int?,
    val feePercentage: BigDecimal?,
    val availableForBundles: List<String>,
    val active: Boolean,
    val sortOrder: Int,
    val updatedAt: LocalDateTime?,
)

data class CreateBundleRequest(
    @field:NotBlank(message = "Id é obrigatório")
    @field:Pattern(regexp = "^[a-z0-9_-]+$", message = "Id deve conter apenas letras minúsculas, números, hífen ou underscore")
    @field:Size(max = 50, message = "Id deve ter no máximo 50 caracteres")
    val id: String,
    @field:NotBlank(message = "Nome é obrigatório")
    @field:Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    val name: String,
    @field:Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    val description: String? = null,
    val modules: List<String> = emptyList(),
    @field:Min(value = 0, message = "Preço mensal deve ser maior ou igual a zero")
    val priceMonthlyCents: Int = 0,
    @field:Min(value = 0, message = "Preço anual deve ser maior ou igual a zero")
    val priceYearlyCents: Int = 0,
    @field:Min(value = 1, message = "Vagas inclusas devem ser pelo menos 1")
    val seatsIncluded: Int = 1,
    @field:Min(value = 0, message = "Slots de tracking devem ser maior ou igual a zero")
    val trackingSlotsIncluded: Int = 0,
    val highlighted: Boolean = false,
    val sortOrder: Int = 0,
    @field:Size(max = 500, message = "Notas devem ter no máximo 500 caracteres")
    val notes: String? = null,
)

data class CreateAddonRequest(
    @field:NotBlank(message = "Id é obrigatório")
    @field:Pattern(regexp = "^[a-z0-9_-]+$", message = "Id deve conter apenas letras minúsculas, números, hífen ou underscore")
    @field:Size(max = 50, message = "Id deve ter no máximo 50 caracteres")
    val id: String,
    @field:NotBlank(message = "Nome é obrigatório")
    @field:Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    val name: String,
    @field:Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    val description: String? = null,
    @field:NotNull(message = "Tipo é obrigatório")
    val type: AddonType,
    val targetModuleId: String? = null,
    @field:Min(value = 0, message = "Preço mensal deve ser maior ou igual a zero")
    val priceMonthlyCents: Int = 0,
    @field:Min(value = 0, message = "Preço unitário deve ser maior ou igual a zero")
    val priceUnitCents: Int? = null,
    @field:DecimalMin(value = "0.0", message = "Taxa percentual deve ser maior ou igual a zero")
    val feePercentage: BigDecimal? = null,
    val availableForBundles: List<String> = emptyList(),
    val sortOrder: Int = 0,
    @field:Size(max = 500, message = "Notas devem ter no máximo 500 caracteres")
    val notes: String? = null,
)

data class ArchiveCatalogRequest(
    @field:Size(max = 500, message = "Notas devem ter no máximo 500 caracteres")
    val notes: String? = null,
)

data class UpdateAddonRequest(
    @field:Min(value = 0, message = "Preço mensal deve ser maior ou igual a zero")
    val priceMonthlyCents: Int? = null,
    @field:Min(value = 0, message = "Preço unitário deve ser maior ou igual a zero")
    val priceUnitCents: Int? = null,
    @field:DecimalMin(value = "0.0", message = "Taxa percentual deve ser maior ou igual a zero")
    val feePercentage: BigDecimal? = null,
    val active: Boolean? = null,
    val availableForBundles: List<String>? = null,
    val sortOrder: Int? = null,
    @field:Size(max = 500, message = "Notas devem ter no máximo 500 caracteres")
    val notes: String? = null,
)
