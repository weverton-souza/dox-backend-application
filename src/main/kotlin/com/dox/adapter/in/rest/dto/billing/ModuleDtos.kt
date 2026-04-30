package com.dox.adapter.`in`.rest.dto.billing

data class ModuleCatalogResponse(
    val id: String,
    val displayName: String,
    val basePriceMonthlyCents: Int,
    val dependencies: Set<String>,
    val gracePeriodDays: Int,
    val gracefulDegradation: String,
)

data class ModuleAccessResponse(
    val id: String,
    val displayName: String,
    val accessLevel: String,
)

data class ActiveModuleResponse(
    val id: String,
    val displayName: String,
)
