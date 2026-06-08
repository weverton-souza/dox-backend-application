package com.dox.application.port.output

import com.dox.domain.billing.TenantAddon
import java.util.UUID

interface TenantAddonPersistencePort {
    fun activeQuantity(
        tenantId: UUID,
        addonId: String,
    ): Int

    fun findByTenantAndAddon(
        tenantId: UUID,
        addonId: String,
    ): TenantAddon?

    fun save(tenantAddon: TenantAddon): TenantAddon
}
