package com.dox.application.port.input

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
}
