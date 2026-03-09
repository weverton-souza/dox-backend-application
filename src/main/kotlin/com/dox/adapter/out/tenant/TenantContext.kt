package com.dox.adapter.out.tenant

object TenantContext {
    private val currentTenant = ThreadLocal<String>()

    fun setTenantId(schemaName: String) = currentTenant.set(schemaName)

    fun getTenantId(): String? = currentTenant.get()

    fun clear() = currentTenant.remove()

    fun convertToSchemaName(tenantId: String): String =
        if (tenantId == "public") "public"
        else "_${tenantId.replace("-", "")}"
}
