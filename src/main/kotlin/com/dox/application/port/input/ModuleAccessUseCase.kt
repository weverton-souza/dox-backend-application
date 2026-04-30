package com.dox.application.port.input

import com.dox.domain.billing.AccessLevel
import com.dox.domain.billing.Module
import com.dox.domain.billing.ModuleSource
import com.dox.domain.billing.TenantModule
import java.time.LocalDateTime
import java.util.UUID

data class ActivateModuleCommand(
    val tenantId: UUID,
    val moduleId: String,
    val source: ModuleSource,
    val sourceId: String? = null,
    val expiresAt: LocalDateTime? = null,
)

interface ModuleAccessUseCase {
    fun catalog(): List<Module>

    fun getActiveModules(tenantId: UUID): Set<Module>

    fun getAccessibleModules(tenantId: UUID): Map<Module, AccessLevel>

    fun getAccessLevel(
        tenantId: UUID,
        moduleId: String,
    ): AccessLevel

    fun hasAccess(
        tenantId: UUID,
        moduleId: String,
    ): Boolean

    fun activate(command: ActivateModuleCommand): TenantModule

    fun deactivate(
        tenantId: UUID,
        moduleId: String,
        reason: String,
    )
}
