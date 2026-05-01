package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.BillingAuditLogJpaEntity
import com.dox.adapter.out.persistence.repository.BillingAuditLogJpaRepository
import com.dox.application.port.output.BillingAuditLogPersistencePort
import com.dox.domain.billing.BillingAuditAction
import com.dox.domain.billing.BillingAuditLog
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class BillingAuditLogPersistenceAdapter(
    private val repository: BillingAuditLogJpaRepository,
) : BillingAuditLogPersistencePort {
    override fun save(log: BillingAuditLog): BillingAuditLog {
        val entity =
            BillingAuditLogJpaEntity(
                id = log.id,
                tenantId = log.tenantId,
                actorAdminId = log.actorAdminId,
                action = log.action.name,
                beforeState = log.beforeState,
                afterState = log.afterState,
                notes = log.notes,
            )
        return repository.save(entity).toDomain()
    }

    override fun findByTenantId(
        tenantId: UUID,
        limit: Int,
    ): List<BillingAuditLog> =
        repository.findByTenantIdOrderByCreatedAtDesc(tenantId, PageRequest.of(0, limit))
            .map { it.toDomain() }

    private fun BillingAuditLogJpaEntity.toDomain() =
        BillingAuditLog(
            id = id,
            tenantId = tenantId,
            actorAdminId = actorAdminId,
            action = BillingAuditAction.valueOf(action),
            beforeState = beforeState,
            afterState = afterState,
            notes = notes,
            createdAt = createdAt,
        )
}
