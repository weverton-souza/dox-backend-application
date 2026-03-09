package com.dox.adapter.out.persistence.adapter

import com.dox.application.port.output.TenantProvisioningPort
import com.dox.config.FlywayTenantConfig
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class TenantProvisioningAdapter(
    private val jdbcTemplate: JdbcTemplate,
    private val flywayTenantConfig: FlywayTenantConfig
) : TenantProvisioningPort {

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    override fun createSchema(schemaName: String) {
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS \"$schemaName\"")
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    override fun runMigrations(schemaName: String) {
        flywayTenantConfig.migrateForTenant(schemaName)
    }
}
