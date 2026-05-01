package com.dox.adapter.`in`.rest.impl.admin

import com.dox.adapter.`in`.rest.dto.admin.AdminModuleListItem
import com.dox.adapter.`in`.rest.dto.admin.AdminModulePriceResponse
import com.dox.adapter.`in`.rest.dto.admin.UpdateModulePriceRequest
import com.dox.adapter.`in`.rest.resource.admin.AdminCatalogResource
import com.dox.application.port.input.AdminCatalogUseCase
import com.dox.application.port.input.AdminModuleCatalogItem
import com.dox.application.port.input.UpdateModulePriceCommand
import com.dox.domain.billing.ModulePrice
import com.dox.shared.ContextHolder
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminCatalogResourceImpl(
    private val adminCatalogUseCase: AdminCatalogUseCase,
) : AdminCatalogResource {
    companion object {
        private const val MAX_HISTORY_LIMIT = 100
    }

    override fun listModules(): ResponseEntity<List<AdminModuleListItem>> = responseEntity(adminCatalogUseCase.listModules().map { it.toListItem() })

    override fun updateModulePrice(
        moduleId: String,
        request: UpdateModulePriceRequest,
    ): ResponseEntity<AdminModulePriceResponse> {
        val actorAdminId = ContextHolder.getUserIdOrThrow()
        val saved =
            adminCatalogUseCase.updateModulePrice(
                moduleId = moduleId,
                command = UpdateModulePriceCommand(priceCents = request.priceCents, notes = request.notes),
                actorAdminId = actorAdminId,
            )
        return responseEntity(saved.toResponse())
    }

    override fun listModulePriceHistory(
        moduleId: String,
        limit: Int,
    ): ResponseEntity<List<AdminModulePriceResponse>> {
        val safeLimit = limit.coerceIn(1, MAX_HISTORY_LIMIT)
        val history = adminCatalogUseCase.listModulePriceHistory(moduleId, safeLimit)
        return responseEntity(history.map { it.toResponse() })
    }

    private fun AdminModuleCatalogItem.toListItem() =
        AdminModuleListItem(
            moduleId = module.id,
            displayName = module.displayName,
            dependencies = module.dependencies.toList(),
            basePriceCents = module.basePriceMonthlyCents,
            currentPriceCents = currentPrice?.priceCents,
            currentValidFrom = currentPrice?.validFrom,
            hasFallback = currentPrice == null,
        )

    private fun ModulePrice.toResponse() =
        AdminModulePriceResponse(
            id = id ?: throw IllegalStateException("ModulePrice salvo deve ter id"),
            moduleId = moduleId,
            priceCents = priceCents,
            currency = currency,
            validFrom = validFrom,
            validUntil = validUntil,
            notes = notes,
            createdByUserId = createdByUserId,
            createdAt = createdAt,
        )
}
