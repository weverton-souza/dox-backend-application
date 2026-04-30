package com.dox.application.port.output

import com.dox.domain.billing.TenantModule
import java.util.UUID

interface TenantModulePersistencePort {
    fun findByTenantId(tenantId: UUID): List<TenantModule>

    fun findByTenantIdAndModuleId(
        tenantId: UUID,
        moduleId: String,
    ): TenantModule?

    fun save(tenantModule: TenantModule): TenantModule

    fun delete(id: UUID)
}
