package com.dox.domain.billing

import java.time.LocalDateTime
import java.util.UUID

data class NfseInvoice(
    val id: UUID = UUID.randomUUID(),
    val tenantId: UUID,
    val paymentId: UUID,
    val asaasInvoiceId: String? = null,
    val status: String,
    val pdfUrl: String? = null,
    val xmlUrl: String? = null,
    val error: String? = null,
    val issuedAt: LocalDateTime? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
