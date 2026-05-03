package com.dox.application.service

import com.dox.application.port.output.TenantPersistencePort
import com.dox.application.port.output.TenantProvisioningPort
import com.dox.domain.enum.TenantType
import com.dox.domain.enum.Vertical
import com.dox.domain.model.Tenant
import com.dox.shared.TenantContext
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TenantProvisioningService(
    private val tenantPersistencePort: TenantPersistencePort,
    private val tenantProvisioningPort: TenantProvisioningPort,
) {
    fun provisionTenant(
        name: String,
        type: TenantType,
        vertical: Vertical,
    ): Tenant {
        val tenantId = UUID.randomUUID()
        val schemaName = TenantContext.convertToSchemaName(tenantId.toString())

        val tenant =
            tenantPersistencePort.save(
                Tenant(
                    id = tenantId,
                    schemaName = schemaName,
                    type = type,
                    name = name,
                    vertical = vertical,
                ),
            )

        tenantProvisioningPort.createSchema(schemaName)
        tenantProvisioningPort.runMigrations(schemaName)

        return tenant
    }
}
