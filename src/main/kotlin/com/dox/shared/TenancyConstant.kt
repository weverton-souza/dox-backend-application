package com.dox.shared

object TenancyConstant {
    const val PUBLIC_SCHEMA = "public"
    const val FLYWAY_PUBLIC_LOCATION = "classpath:db/migration/public"
    const val FLYWAY_SCHEMAS_LOCATION = "classpath:db/migration/schemas"
    const val TENANT_ID = "TENANT_ID"
    const val USER_KEY = "USER_KEY"

    private val VALID_SCHEMA_PATTERN = Regex("^_[a-f0-9]{32}$")

    fun validateSchemaName(schemaName: String): String {
        if (schemaName == PUBLIC_SCHEMA) return schemaName
        require(VALID_SCHEMA_PATTERN.matches(schemaName)) {
            "Schema name inválido: formato inesperado"
        }
        return schemaName
    }
}
