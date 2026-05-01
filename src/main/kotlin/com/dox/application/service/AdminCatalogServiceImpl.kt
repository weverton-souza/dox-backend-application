package com.dox.application.service

import com.dox.application.port.input.AdminCatalogUseCase
import com.dox.application.port.input.AdminModuleCatalogItem
import com.dox.application.port.input.UpdateModulePriceCommand
import com.dox.application.port.output.BillingAuditLogPersistencePort
import com.dox.application.port.output.ModulePricePersistencePort
import com.dox.domain.billing.BillingAuditAction
import com.dox.domain.billing.BillingAuditLog
import com.dox.domain.billing.Module
import com.dox.domain.billing.ModulePrice
import com.dox.domain.exception.BusinessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class AdminCatalogServiceImpl(
    private val modulePricePersistencePort: ModulePricePersistencePort,
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

    private fun ModulePrice.toAuditMap(): Map<String, Any?> =
        mapOf(
            "moduleId" to moduleId,
            "priceCents" to priceCents,
            "currency" to currency,
            "validFrom" to validFrom.toString(),
            "validUntil" to validUntil?.toString(),
            "notes" to notes,
        )
}
