package com.dox.application.port.input

import com.dox.domain.billing.Bundle
import com.dox.domain.billing.Module
import com.dox.domain.billing.ModulePrice
import java.util.UUID

data class AdminModuleCatalogItem(
    val module: Module,
    val currentPrice: ModulePrice?,
)

data class UpdateModulePriceCommand(
    val priceCents: Int,
    val notes: String?,
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

    fun updateBundle(
        bundleId: String,
        command: UpdateBundleCommand,
        actorAdminId: UUID,
    ): Bundle
}
