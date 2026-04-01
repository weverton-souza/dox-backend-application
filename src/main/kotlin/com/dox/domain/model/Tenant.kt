package com.dox.domain.model

import com.dox.domain.enum.TenantType
import com.dox.domain.enum.Vertical
import java.time.LocalDateTime
import java.util.UUID

data class Tenant(
    val id: UUID = UUID.randomUUID(),
    val schemaName: String,
    val type: TenantType,
    val name: String,
    val vertical: Vertical = Vertical.GENERAL,
    val createdAt: LocalDateTime? = null,
)
