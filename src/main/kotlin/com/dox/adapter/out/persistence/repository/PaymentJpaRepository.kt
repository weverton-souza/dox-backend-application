package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.PaymentJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
interface PaymentJpaRepository : JpaRepository<PaymentJpaEntity, UUID> {
    fun findByAsaasPaymentId(asaasPaymentId: String): PaymentJpaEntity?

    fun findByTenantIdAndDueDateBetweenOrderByDueDateDesc(
        tenantId: UUID,
        from: LocalDate,
        to: LocalDate,
    ): List<PaymentJpaEntity>

    fun findByTenantIdOrderByDueDateDesc(tenantId: UUID): List<PaymentJpaEntity>
}
