package com.dox.application.port.output

interface TenantProvisioningPort {
    fun createSchema(schemaName: String)

    fun runMigrations(schemaName: String)
}
