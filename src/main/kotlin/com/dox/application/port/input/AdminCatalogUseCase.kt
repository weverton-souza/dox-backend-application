package com.dox.application.port.input

import com.dox.domain.billing.Addon
import com.dox.domain.billing.AddonType
import com.dox.domain.billing.Bundle
import com.dox.domain.billing.Module
import com.dox.domain.billing.ModulePrice
import java.math.BigDecimal
import java.util.UUID

data class AdminModuleCatalogItem(
    val module: Module,
    val currentPrice: ModulePrice?,
)

data class UpdateModulePriceCommand(
    val priceCents: Int,
    val notes: String?,
)

data class CreateBundleCommand(
    val id: String,
    val name: String,
    val description: String? = null,
    val modules: List<String>,
    val priceMonthlyCents: Int,
    val priceYearlyCents: Int,
    val seatsIncluded: Int = 1,
    val trackingSlotsIncluded: Int = 0,
    val highlighted: Boolean = false,
    val sortOrder: Int = 0,
    val notes: String? = null,
)

data class UpdateBundleCommand(
    val priceMonthlyCents: Int? = null,
    val priceYearlyCents: Int? = null,
    val description: String? = null,
    val seatsIncluded: Int? = null,
    val trackingSlotsIncluded: Int? = null,
    val highlighted: Boolean? = null,
    val sortOrder: Int? = null,
    val notes: String? = null,
)

data class CreateAddonCommand(
    val id: String,
    val name: String,
    val description: String? = null,
    val type: AddonType,
    val targetModuleId: String? = null,
    val priceMonthlyCents: Int = 0,
    val priceUnitCents: Int? = null,
    val feePercentage: BigDecimal? = null,
    val availableForBundles: List<String> = emptyList(),
    val sortOrder: Int = 0,
    val notes: String? = null,
)

data class UpdateAddonCommand(
    val priceMonthlyCents: Int? = null,
    val priceUnitCents: Int? = null,
    val feePercentage: BigDecimal? = null,
    val active: Boolean? = null,
    val availableForBundles: List<String>? = null,
    val sortOrder: Int? = null,
    val notes: String? = null,
)

interface AdminCatalogUseCase {
    fun listModules(): List<AdminModuleCatalogItem>

    fun updateModulePrice(
        moduleId: String,
        command: UpdateModulePriceCommand,
        actorAdminId: UUID,
    ): ModulePrice

    fun listModulePriceHistory(
        moduleId: String,
        limit: Int,
    ): List<ModulePrice>

    fun listBundles(): List<Bundle>

    fun createBundle(
        command: CreateBundleCommand,
        actorAdminId: UUID,
    ): Bundle

    fun updateBundle(
        bundleId: String,
        command: UpdateBundleCommand,
        actorAdminId: UUID,
    ): Bundle

    fun archiveBundle(
        bundleId: String,
        actorAdminId: UUID,
        notes: String?,
    ): Bundle

    fun listAddons(): List<Addon>

    fun createAddon(
        command: CreateAddonCommand,
        actorAdminId: UUID,
    ): Addon

    fun updateAddon(
        addonId: String,
        command: UpdateAddonCommand,
        actorAdminId: UUID,
    ): Addon

    fun archiveAddon(
        addonId: String,
        actorAdminId: UUID,
        notes: String?,
    ): Addon
}
