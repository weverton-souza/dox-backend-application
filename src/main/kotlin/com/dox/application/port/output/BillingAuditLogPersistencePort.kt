package com.dox.application.port.output

import com.dox.domain.billing.BillingAuditLog
import java.util.UUID

interface BillingAuditLogPersistencePort {
    fun save(log: BillingAuditLog): BillingAuditLog

    fun findByTenantId(
        tenantId: UUID,
        limit: Int,
    ): List<BillingAuditLog>
}
