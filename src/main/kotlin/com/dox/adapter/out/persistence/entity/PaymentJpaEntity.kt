package com.dox.adapter.out.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "payments", schema = "public")
@EntityListeners(AuditingEntityListener::class)
class PaymentJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "tenant_id", nullable = false)
    var tenantId: UUID,
    @Column(name = "subscription_id")
    var subscriptionId: UUID? = null,
    @Column(name = "asaas_payment_id", nullable = false, unique = true, length = 255)
    var asaasPaymentId: String,
    @Column(name = "amount_cents", nullable = false)
    var amountCents: Int,
    @Column(name = "status", nullable = false, length = 30)
    var status: String,
    @Column(name = "billing_type", nullable = false, length = 20)
    var billingType: String,
    @Column(name = "due_date", nullable = false)
    var dueDate: LocalDate,
    @Column(name = "paid_at")
    var paidAt: LocalDateTime? = null,
    @Column(name = "refunded_at")
    var refundedAt: LocalDateTime? = null,
    @Column(name = "invoice_url", columnDefinition = "TEXT")
    var invoiceUrl: String? = null,
    @Column(name = "bank_slip_url", columnDefinition = "TEXT")
    var bankSlipUrl: String? = null,
    @Column(name = "pix_qr_code", columnDefinition = "TEXT")
    var pixQrCode: String? = null,
    @Column(name = "pix_copy_paste", columnDefinition = "TEXT")
    var pixCopyPaste: String? = null,
    @Column(name = "description", length = 500)
    var description: String? = null,
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
)
