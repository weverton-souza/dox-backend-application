package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.BillingAuditLogJpaEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface BillingAuditLogJpaRepository : JpaRepository<BillingAuditLogJpaEntity, UUID> {
    fun findByTenantIdOrderByCreatedAtDesc(
        tenantId: UUID,
        pageable: Pageable,
    ): List<BillingAuditLogJpaEntity>
}
