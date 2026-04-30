package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.NfseInvoiceJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface NfseInvoiceJpaRepository : JpaRepository<NfseInvoiceJpaEntity, UUID> {
    fun findByTenantIdOrderByCreatedAtDesc(tenantId: UUID): List<NfseInvoiceJpaEntity>

    fun findByPaymentId(paymentId: UUID): NfseInvoiceJpaEntity?
}
