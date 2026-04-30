package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.NfseInvoiceJpaEntity
import com.dox.adapter.out.persistence.repository.NfseInvoiceJpaRepository
import com.dox.application.port.output.NfseInvoicePersistencePort
import com.dox.domain.billing.NfseInvoice
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class NfseInvoicePersistenceAdapter(
    private val repository: NfseInvoiceJpaRepository,
) : NfseInvoicePersistencePort {
    override fun findByTenantId(tenantId: UUID): List<NfseInvoice> = repository.findByTenantIdOrderByCreatedAtDesc(tenantId).map { it.toDomain() }

    override fun findByPaymentId(paymentId: UUID): NfseInvoice? = repository.findByPaymentId(paymentId)?.toDomain()

    override fun save(invoice: NfseInvoice): NfseInvoice {
        val entity =
            repository.findByPaymentId(invoice.paymentId) ?: NfseInvoiceJpaEntity(
                id = invoice.id,
                tenantId = invoice.tenantId,
                paymentId = invoice.paymentId,
                status = invoice.status,
            )
        entity.asaasInvoiceId = invoice.asaasInvoiceId
        entity.status = invoice.status
        entity.pdfUrl = invoice.pdfUrl
        entity.xmlUrl = invoice.xmlUrl
        entity.error = invoice.error
        entity.issuedAt = invoice.issuedAt
        return repository.save(entity).toDomain()
    }

    private fun NfseInvoiceJpaEntity.toDomain() =
        NfseInvoice(
            id = id,
            tenantId = tenantId,
            paymentId = paymentId,
            asaasInvoiceId = asaasInvoiceId,
            status = status,
            pdfUrl = pdfUrl,
            xmlUrl = xmlUrl,
            error = error,
            issuedAt = issuedAt,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
}
