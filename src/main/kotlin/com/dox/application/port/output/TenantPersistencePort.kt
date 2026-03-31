package com.dox.application.port.output

import com.dox.domain.model.Tenant
import java.util.UUID

interface TenantPersistencePort {
    fun save(tenant: Tenant): Tenant

    fun findById(id: UUID): Tenant?

    fun findBySchemaName(schemaName: String): Tenant?
}
