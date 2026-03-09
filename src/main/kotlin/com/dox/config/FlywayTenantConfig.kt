package com.dox.config

import com.dox.shared.TenancyConstant
import org.flywaydb.core.Flyway
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class FlywayTenantConfig(
    private val dataSource: DataSource
) {

    @Bean
    fun flywayMigrationStrategy(): FlywayMigrationStrategy {
        return FlywayMigrationStrategy {
            val publicFlyway = Flyway.configure()
                .locations(TenancyConstant.FLYWAY_PUBLIC_LOCATION)
                .dataSource(dataSource)
                .schemas(TenancyConstant.PUBLIC_SCHEMA)
                .baselineOnMigrate(true)
                .load()
            publicFlyway.migrate()
        }
    }

    fun migrateForTenant(schemaName: String) {
        Flyway.configure()
            .locations(TenancyConstant.FLYWAY_SCHEMAS_LOCATION)
            .dataSource(dataSource)
            .schemas(schemaName)
            .baselineOnMigrate(true)
            .load()
            .migrate()
    }
}
