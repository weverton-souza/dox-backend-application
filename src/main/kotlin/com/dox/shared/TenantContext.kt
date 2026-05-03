package com.dox.shared

import java.util.UUID

object TenantContext {
    private val currentTenant = ThreadLocal<String>()

    fun setTenantId(schemaName: String) = currentTenant.set(TenancyConstant.validateSchemaName(schemaName))

    fun getTenantId(): String? = currentTenant.get()

    fun clear() = currentTenant.remove()

    fun convertToSchemaName(tenantId: String): String =
        if (tenantId == TenancyConstant.PUBLIC_SCHEMA) {
            TenancyConstant.PUBLIC_SCHEMA
        } else {
            "_${tenantId.replace("-", "")}"
        }

    fun <T> withTenantContext(
        tenantId: UUID,
        block: () -> T,
    ): T {
        setTenantId(convertToSchemaName(tenantId.toString()))
        return try {
            block()
        } finally {
            clear()
        }
    }
}
