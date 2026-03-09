package com.dox.shared

object TenancyConstant {
    const val PUBLIC_SCHEMA = "public"
    const val FLYWAY_PUBLIC_LOCATION = "classpath:db/migration/public"
    const val FLYWAY_SCHEMAS_LOCATION = "classpath:db/migration/schemas"
    const val TENANT_ID = "TENANT_ID"
    const val USER_KEY = "USER_KEY"
}
