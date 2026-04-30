package com.dox.domain.billing

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class Payment(
    val id: UUID = UUID.randomUUID(),
    val tenantId: UUID,
    val subscriptionId: UUID? = null,
    val asaasPaymentId: String,
    val amountCents: Int,
    val status: String,
    val billingType: BillingType,
    val dueDate: LocalDate,
    val paidAt: LocalDateTime? = null,
    val refundedAt: LocalDateTime? = null,
    val invoiceUrl: String? = null,
    val bankSlipUrl: String? = null,
    val pixQrCode: String? = null,
    val pixCopyPaste: String? = null,
    val description: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
