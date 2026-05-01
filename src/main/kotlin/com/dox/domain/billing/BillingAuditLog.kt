package com.dox.domain.billing

import java.time.LocalDateTime
import java.util.UUID

data class BillingAuditLog(
    val id: UUID = UUID.randomUUID(),
    val tenantId: UUID? = null,
    val actorAdminId: UUID,
    val action: BillingAuditAction,
    val beforeState: Map<String, Any?>? = null,
    val afterState: Map<String, Any?>? = null,
    val notes: String? = null,
    val createdAt: LocalDateTime? = null,
)
