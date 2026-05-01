package com.dox.application.service

import com.dox.application.port.input.AdminCatalogUseCase
import com.dox.application.port.input.AdminModuleCatalogItem
import com.dox.application.port.input.UpdateAddonCommand
import com.dox.application.port.input.UpdateBundleCommand
import com.dox.application.port.input.UpdateModulePriceCommand
import com.dox.application.port.output.AddonPersistencePort
import com.dox.application.port.output.BillingAuditLogPersistencePort
import com.dox.application.port.output.BundlePersistencePort
import com.dox.application.port.output.ModulePricePersistencePort
import com.dox.domain.billing.Addon
import com.dox.domain.billing.BillingAuditAction
import com.dox.domain.billing.BillingAuditLog
import com.dox.domain.billing.Bundle
import com.dox.domain.billing.Module
import com.dox.domain.billing.ModulePrice
import com.dox.domain.exception.BusinessException
import com.dox.domain.exception.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class AdminCatalogServiceImpl(
    private val modulePricePersistencePort: ModulePricePersistencePort,
    private val bundlePersistencePort: BundlePersistencePort,
    private val addonPersistencePort: AddonPersistencePort,
    private val billingAuditLogPersistencePort: BillingAuditLogPersistencePort,
) : AdminCatalogUseCase {
    @Transactional(readOnly = true)
    override fun listModules(): List<AdminModuleCatalogItem> =
        Module.entries.map { module ->
            AdminModuleCatalogItem(
                module = module,
                currentPrice = modulePricePersistencePort.findCurrentPrice(module.id),
            )
        }

    @Transactional
    override fun updateModulePrice(
        moduleId: String,
        command: UpdateModulePriceCommand,
        actorAdminId: UUID,
    ): ModulePrice {
        Module.fromId(moduleId)
            ?: throw BusinessException("Módulo desconhecido: $moduleId")

        if (command.priceCents < 0) {
            throw BusinessException("Preço deve ser maior ou igual a zero")
        }

        val before = modulePricePersistencePort.findCurrentPrice(moduleId)
        val now = LocalDateTime.now()

        modulePricePersistencePort.expireCurrent(moduleId, now)

        val newPrice =
            modulePricePersistencePort.save(
                ModulePrice(
                    moduleId = moduleId,
                    priceCents = command.priceCents,
                    validFrom = now,
                    validUntil = null,
                    notes = command.notes,
                    createdByUserId = actorAdminId,
                ),
            )

        billingAuditLogPersistencePort.save(
            BillingAuditLog(
                tenantId = null,
                actorAdminId = actorAdminId,
                action = BillingAuditAction.EDIT_MODULE_PRICE,
                beforeState = before?.toAuditMap() ?: mapOf("moduleId" to moduleId, "previous" to "fallback-enum"),
                afterState = newPrice.toAuditMap(),
                notes = command.notes,
            ),
        )

        return newPrice
    }

    @Transactional(readOnly = true)
    override fun listModulePriceHistory(
        moduleId: String,
        limit: Int,
    ): List<ModulePrice> {
        Module.fromId(moduleId)
            ?: throw BusinessException("Módulo desconhecido: $moduleId")
        return modulePricePersistencePort.findHistory(moduleId, limit)
    }

    @Transactional(readOnly = true)
    override fun listBundles(): List<Bundle> = bundlePersistencePort.findAll()

    @Transactional
    override fun updateBundle(
        bundleId: String,
        command: UpdateBundleCommand,
        actorAdminId: UUID,
    ): Bundle {
        val existing =
            bundlePersistencePort.findById(bundleId)
                ?: throw ResourceNotFoundException("Bundle", bundleId)

        command.priceMonthlyCents?.let {
            if (it < 0) throw BusinessException("Preço mensal deve ser maior ou igual a zero")
        }
        command.priceYearlyCents?.let {
            if (it < 0) throw BusinessException("Preço anual deve ser maior ou igual a zero")
        }
        command.seatsIncluded?.let {
            if (it < 1) throw BusinessException("Vagas inclusas devem ser pelo menos 1")
        }
        command.trackingSlotsIncluded?.let {
            if (it < 0) throw BusinessException("Slots de tracking devem ser maior ou igual a zero")
        }

        val updated =
            existing.copy(
                priceMonthlyCents = command.priceMonthlyCents ?: existing.priceMonthlyCents,
                priceYearlyCents = command.priceYearlyCents ?: existing.priceYearlyCents,
                description = command.description ?: existing.description,
                seatsIncluded = command.seatsIncluded ?: existing.seatsIncluded,
                trackingSlotsIncluded = command.trackingSlotsIncluded ?: existing.trackingSlotsIncluded,
                highlighted = command.highlighted ?: existing.highlighted,
                sortOrder = command.sortOrder ?: existing.sortOrder,
            )

        val saved = bundlePersistencePort.save(updated)

        billingAuditLogPersistencePort.save(
            BillingAuditLog(
                tenantId = null,
                actorAdminId = actorAdminId,
                action = BillingAuditAction.EDIT_BUNDLE,
                beforeState = existing.toAuditMap(),
                afterState = saved.toAuditMap(),
                notes = command.notes,
            ),
        )

        return saved
    }

    private fun ModulePrice.toAuditMap(): Map<String, Any?> =
        mapOf(
            "moduleId" to moduleId,
            "priceCents" to priceCents,
            "currency" to currency,
            "validFrom" to validFrom.toString(),
            "validUntil" to validUntil?.toString(),
            "notes" to notes,
        )

    private fun Bundle.toAuditMap(): Map<String, Any?> =
        mapOf(
            "id" to id,
            "name" to name,
            "description" to description,
            "priceMonthlyCents" to priceMonthlyCents,
            "priceYearlyCents" to priceYearlyCents,
            "seatsIncluded" to seatsIncluded,
            "trackingSlotsIncluded" to trackingSlotsIncluded,
            "highlighted" to highlighted,
            "active" to active,
            "sortOrder" to sortOrder,
        )

    @Transactional(readOnly = true)
    override fun listAddons(): List<Addon> = addonPersistencePort.findAll()

    @Transactional
    override fun updateAddon(
        addonId: String,
        command: UpdateAddonCommand,
        actorAdminId: UUID,
    ): Addon {
        val existing =
            addonPersistencePort.findById(addonId)
                ?: throw ResourceNotFoundException("Addon", addonId)

        command.priceMonthlyCents?.let {
            if (it < 0) throw BusinessException("Preço mensal deve ser maior ou igual a zero")
        }
        command.priceUnitCents?.let {
            if (it < 0) throw BusinessException("Preço unitário deve ser maior ou igual a zero")
        }
        command.feePercentage?.let {
            if (it.signum() < 0) throw BusinessException("Taxa percentual deve ser maior ou igual a zero")
        }

        val updated =
            existing.copy(
                priceMonthlyCents = command.priceMonthlyCents ?: existing.priceMonthlyCents,
                priceUnitCents = command.priceUnitCents ?: existing.priceUnitCents,
                feePercentage = command.feePercentage ?: existing.feePercentage,
                active = command.active ?: existing.active,
                availableForBundles = command.availableForBundles ?: existing.availableForBundles,
                sortOrder = command.sortOrder ?: existing.sortOrder,
            )

        val saved = addonPersistencePort.save(updated)

        billingAuditLogPersistencePort.save(
            BillingAuditLog(
                tenantId = null,
                actorAdminId = actorAdminId,
                action = BillingAuditAction.EDIT_ADDON,
                beforeState = existing.toAuditMap(),
                afterState = saved.toAuditMap(),
                notes = command.notes,
            ),
        )

        return saved
    }

    private fun Addon.toAuditMap(): Map<String, Any?> =
        mapOf(
            "id" to id,
            "name" to name,
            "type" to type.name,
            "targetModuleId" to targetModuleId,
            "priceMonthlyCents" to priceMonthlyCents,
            "priceUnitCents" to priceUnitCents,
            "feePercentage" to feePercentage?.toPlainString(),
            "availableForBundles" to availableForBundles,
            "active" to active,
            "sortOrder" to sortOrder,
        )
}
