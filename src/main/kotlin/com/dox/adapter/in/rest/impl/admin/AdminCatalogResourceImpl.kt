package com.dox.adapter.`in`.rest.impl.admin

import com.dox.adapter.`in`.rest.dto.admin.AdminAddonListItem
import com.dox.adapter.`in`.rest.dto.admin.AdminBundleListItem
import com.dox.adapter.`in`.rest.dto.admin.AdminModuleListItem
import com.dox.adapter.`in`.rest.dto.admin.AdminModulePriceResponse
import com.dox.adapter.`in`.rest.dto.admin.UpdateAddonRequest
import com.dox.adapter.`in`.rest.dto.admin.UpdateBundleRequest
import com.dox.adapter.`in`.rest.dto.admin.UpdateModulePriceRequest
import com.dox.adapter.`in`.rest.resource.admin.AdminCatalogResource
import com.dox.application.port.input.AdminCatalogUseCase
import com.dox.application.port.input.AdminModuleCatalogItem
import com.dox.application.port.input.UpdateAddonCommand
import com.dox.application.port.input.UpdateBundleCommand
import com.dox.application.port.input.UpdateModulePriceCommand
import com.dox.domain.billing.Addon
import com.dox.domain.billing.Bundle
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

    override fun listBundles(): ResponseEntity<List<AdminBundleListItem>> = responseEntity(adminCatalogUseCase.listBundles().map { it.toListItem() })

    override fun updateBundle(
        bundleId: String,
        request: UpdateBundleRequest,
    ): ResponseEntity<AdminBundleListItem> {
        val actorAdminId = ContextHolder.getUserIdOrThrow()
        val saved =
            adminCatalogUseCase.updateBundle(
                bundleId = bundleId,
                command =
                    UpdateBundleCommand(
                        priceMonthlyCents = request.priceMonthlyCents,
                        priceYearlyCents = request.priceYearlyCents,
                        description = request.description,
                        seatsIncluded = request.seatsIncluded,
                        trackingSlotsIncluded = request.trackingSlotsIncluded,
                        highlighted = request.highlighted,
                        sortOrder = request.sortOrder,
                        notes = request.notes,
                    ),
                actorAdminId = actorAdminId,
            )
        return responseEntity(saved.toListItem())
    }

    private fun Bundle.toListItem() =
        AdminBundleListItem(
            id = id,
            name = name,
            description = description,
            modules = modules,
            priceMonthlyCents = priceMonthlyCents,
            priceYearlyCents = priceYearlyCents,
            seatsIncluded = seatsIncluded,
            trackingSlotsIncluded = trackingSlotsIncluded,
            highlighted = highlighted,
            active = active,
            sortOrder = sortOrder,
            updatedAt = updatedAt,
        )

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

    override fun listAddons(): ResponseEntity<List<AdminAddonListItem>> = responseEntity(adminCatalogUseCase.listAddons().map { it.toListItem() })

    override fun updateAddon(
        addonId: String,
        request: UpdateAddonRequest,
    ): ResponseEntity<AdminAddonListItem> {
        val actorAdminId = ContextHolder.getUserIdOrThrow()
        val saved =
            adminCatalogUseCase.updateAddon(
                addonId = addonId,
                command =
                    UpdateAddonCommand(
                        priceMonthlyCents = request.priceMonthlyCents,
                        priceUnitCents = request.priceUnitCents,
                        feePercentage = request.feePercentage,
                        active = request.active,
                        availableForBundles = request.availableForBundles,
                        sortOrder = request.sortOrder,
                        notes = request.notes,
                    ),
                actorAdminId = actorAdminId,
            )
        return responseEntity(saved.toListItem())
    }

    private fun Addon.toListItem() =
        AdminAddonListItem(
            id = id,
            name = name,
            description = description,
            type = type,
            targetModuleId = targetModuleId,
            priceMonthlyCents = priceMonthlyCents,
            priceUnitCents = priceUnitCents,
            feePercentage = feePercentage,
            availableForBundles = availableForBundles,
            active = active,
            sortOrder = sortOrder,
            updatedAt = updatedAt,
        )
}
