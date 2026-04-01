package com.dox.adapter.out.tenant

import com.dox.shared.TenancyConstant
import org.hibernate.cfg.AvailableSettings
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer
import org.springframework.stereotype.Component
import java.sql.Connection
import javax.sql.DataSource

@Component
class TenantConnectionProvider(
    private val dataSource: DataSource,
) : MultiTenantConnectionProvider<String>, HibernatePropertiesCustomizer {
    override fun getAnyConnection(): Connection = dataSource.connection

    override fun releaseAnyConnection(connection: Connection) = connection.close()

    override fun getConnection(tenantIdentifier: String): Connection {
        val sanitized = TenancyConstant.validateSchemaName(tenantIdentifier)
        val connection = anyConnection
        connection.createStatement().use { it.execute("SET search_path TO \"$sanitized\"") }
        return connection
    }

    override fun releaseConnection(
        tenantIdentifier: String,
        connection: Connection,
    ) {
        connection.createStatement().use { it.execute("SET search_path TO \"${TenancyConstant.PUBLIC_SCHEMA}\"") }
        connection.close()
    }

    override fun supportsAggressiveRelease(): Boolean = false

    override fun isUnwrappableAs(unwrapType: Class<*>): Boolean = false

    override fun <T : Any> unwrap(unwrapType: Class<T>): T = throw UnsupportedOperationException()

    override fun customize(hibernateProperties: MutableMap<String, Any>) {
        hibernateProperties[AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER] = this
    }
}
