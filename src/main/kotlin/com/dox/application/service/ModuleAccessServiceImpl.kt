package com.dox.application.service

import com.dox.application.port.input.ActivateModuleCommand
import com.dox.application.port.input.ModuleAccessUseCase
import com.dox.application.port.output.TenantModulePersistencePort
import com.dox.domain.billing.AccessLevel
import com.dox.domain.billing.DegradationMode
import com.dox.domain.billing.Module
import com.dox.domain.billing.ModuleStatus
import com.dox.domain.billing.TenantModule
import com.dox.domain.exception.BusinessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class ModuleAccessServiceImpl(
    private val persistencePort: TenantModulePersistencePort,
) : ModuleAccessUseCase {
    override fun catalog(): List<Module> = Module.entries.toList()

    override fun getActiveModules(tenantId: UUID): Set<Module> =
        persistencePort.findByTenantId(tenantId)
            .filter { it.status in ACTIVE_STATUSES }
            .mapNotNull { Module.fromId(it.moduleId) }
            .toSet()

    override fun getAccessibleModules(tenantId: UUID): Map<Module, AccessLevel> {
        val tenantModules = persistencePort.findByTenantId(tenantId).associateBy { it.moduleId }
        return Module.entries.associateWith { module ->
            val tm = tenantModules[module.id]
            resolveAccessLevel(module, tm)
        }
    }

    override fun getAccessLevel(
        tenantId: UUID,
        moduleId: String,
    ): AccessLevel {
        val module = Module.fromId(moduleId) ?: return AccessLevel.BLOCKED
        val tm = persistencePort.findByTenantIdAndModuleId(tenantId, moduleId)
        return resolveAccessLevel(module, tm)
    }

    override fun hasAccess(
        tenantId: UUID,
        moduleId: String,
    ): Boolean = getAccessLevel(tenantId, moduleId) != AccessLevel.BLOCKED

    @Transactional
    override fun activate(command: ActivateModuleCommand): TenantModule {
        val module = Module.fromId(command.moduleId) ?: throw BusinessException("Módulo '${command.moduleId}' não existe no catálogo")
        validateDependencies(command.tenantId, module)
        val existing = persistencePort.findByTenantIdAndModuleId(command.tenantId, command.moduleId)
        val now = LocalDateTime.now()
        val updated =
            existing?.copy(
                status = ModuleStatus.ACTIVE,
                source = command.source,
                sourceId = command.sourceId,
                activatedAt = now,
                expiresAt = command.expiresAt,
                graceUntil = null,
                canceledAt = null,
                cancelReason = null,
            ) ?: TenantModule(
                tenantId = command.tenantId,
                moduleId = command.moduleId,
                status = ModuleStatus.ACTIVE,
                source = command.source,
                sourceId = command.sourceId,
                activatedAt = now,
                expiresAt = command.expiresAt,
                basePriceCents = module.basePriceMonthlyCents,
                finalPriceCents = module.basePriceMonthlyCents,
            )
        return persistencePort.save(updated)
    }

    @Transactional
    override fun deactivate(
        tenantId: UUID,
        moduleId: String,
        reason: String,
    ) {
        val existing =
            persistencePort.findByTenantIdAndModuleId(tenantId, moduleId)
                ?: throw BusinessException("Módulo '$moduleId' não está ativo neste tenant")
        persistencePort.save(
            existing.copy(
                status = ModuleStatus.CANCELED,
                canceledAt = LocalDateTime.now(),
                cancelReason = reason,
            ),
        )
    }

    private fun validateDependencies(
        tenantId: UUID,
        module: Module,
    ) {
        if (module.dependencies.isEmpty()) return
        val active = getActiveModules(tenantId).map { it.id }.toSet()
        val missing = module.dependencies - active
        if (missing.isNotEmpty()) {
            throw BusinessException("Módulo '${module.id}' depende de: ${missing.joinToString(", ")}")
        }
    }

    private fun resolveAccessLevel(
        module: Module,
        tenantModule: TenantModule?,
    ): AccessLevel {
        if (tenantModule == null) return AccessLevel.FULL
        return when (tenantModule.status) {
            ModuleStatus.TRIAL, ModuleStatus.ACTIVE, ModuleStatus.GRANTED -> AccessLevel.FULL
            ModuleStatus.GRACE -> degradationToAccess(module.gracefulDegradation)
            ModuleStatus.SUSPENDED -> degradationToAccess(module.gracefulDegradation)
            ModuleStatus.CANCELED -> AccessLevel.BLOCKED
        }
    }

    private fun degradationToAccess(mode: DegradationMode): AccessLevel =
        when (mode) {
            DegradationMode.READ_ONLY -> AccessLevel.READ_ONLY
            DegradationMode.LIMITED -> AccessLevel.READ_ONLY
            DegradationMode.BLOCKED -> AccessLevel.BLOCKED
        }

    companion object {
        private val ACTIVE_STATUSES =
            setOf(ModuleStatus.TRIAL, ModuleStatus.ACTIVE, ModuleStatus.GRANTED, ModuleStatus.GRACE)
    }
}
