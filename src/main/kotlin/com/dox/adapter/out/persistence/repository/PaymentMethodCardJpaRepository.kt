package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.PaymentMethodCardJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PaymentMethodCardJpaRepository : JpaRepository<PaymentMethodCardJpaEntity, UUID> {
    fun findByTenantIdOrderByCreatedAtDesc(tenantId: UUID): List<PaymentMethodCardJpaEntity>

    fun findByTenantIdAndIsDefaultTrue(tenantId: UUID): PaymentMethodCardJpaEntity?
}
