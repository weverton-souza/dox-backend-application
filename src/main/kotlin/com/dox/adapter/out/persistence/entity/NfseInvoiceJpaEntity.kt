package com.dox.adapter.out.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "invoices_nfse", schema = "public")
@EntityListeners(AuditingEntityListener::class)
class NfseInvoiceJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "tenant_id", nullable = false)
    var tenantId: UUID,
    @Column(name = "payment_id", nullable = false)
    var paymentId: UUID,
    @Column(name = "asaas_invoice_id", unique = true, length = 255)
    var asaasInvoiceId: String? = null,
    @Column(name = "status", nullable = false, length = 30)
    var status: String,
    @Column(name = "pdf_url", columnDefinition = "TEXT")
    var pdfUrl: String? = null,
    @Column(name = "xml_url", columnDefinition = "TEXT")
    var xmlUrl: String? = null,
    @Column(name = "error", columnDefinition = "TEXT")
    var error: String? = null,
    @Column(name = "issued_at")
    var issuedAt: LocalDateTime? = null,
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
)
