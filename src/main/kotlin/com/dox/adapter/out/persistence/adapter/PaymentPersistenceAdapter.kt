package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.PaymentJpaEntity
import com.dox.adapter.out.persistence.repository.PaymentJpaRepository
import com.dox.application.port.output.PaymentPersistencePort
import com.dox.domain.billing.BillingType
import com.dox.domain.billing.Payment
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.UUID

@Component
class PaymentPersistenceAdapter(
    private val repository: PaymentJpaRepository,
) : PaymentPersistencePort {
    override fun findByAsaasPaymentId(asaasPaymentId: String): Payment? = repository.findByAsaasPaymentId(asaasPaymentId)?.toDomain()

    override fun findByTenantId(tenantId: UUID): List<Payment> = repository.findByTenantIdOrderByDueDateDesc(tenantId).map { it.toDomain() }

    override fun findByTenantIdAndDueDateBetween(
        tenantId: UUID,
        from: LocalDate,
        to: LocalDate,
    ): List<Payment> = repository.findByTenantIdAndDueDateBetweenOrderByDueDateDesc(tenantId, from, to).map { it.toDomain() }

    override fun save(payment: Payment): Payment {
        val entity =
            repository.findByAsaasPaymentId(payment.asaasPaymentId) ?: PaymentJpaEntity(
                id = payment.id,
                tenantId = payment.tenantId,
                subscriptionId = payment.subscriptionId,
                asaasPaymentId = payment.asaasPaymentId,
                amountCents = payment.amountCents,
                status = payment.status,
                billingType = payment.billingType.name,
                dueDate = payment.dueDate,
            )
        entity.subscriptionId = payment.subscriptionId
        entity.amountCents = payment.amountCents
        entity.status = payment.status
        entity.billingType = payment.billingType.name
        entity.dueDate = payment.dueDate
        entity.paidAt = payment.paidAt
        entity.refundedAt = payment.refundedAt
        entity.invoiceUrl = payment.invoiceUrl
        entity.bankSlipUrl = payment.bankSlipUrl
        entity.pixQrCode = payment.pixQrCode
        entity.pixCopyPaste = payment.pixCopyPaste
        entity.description = payment.description
        return repository.save(entity).toDomain()
    }

    private fun PaymentJpaEntity.toDomain() =
        Payment(
            id = id,
            tenantId = tenantId,
            subscriptionId = subscriptionId,
            asaasPaymentId = asaasPaymentId,
            amountCents = amountCents,
            status = status,
            billingType = BillingType.valueOf(billingType),
            dueDate = dueDate,
            paidAt = paidAt,
            refundedAt = refundedAt,
            invoiceUrl = invoiceUrl,
            bankSlipUrl = bankSlipUrl,
            pixQrCode = pixQrCode,
            pixCopyPaste = pixCopyPaste,
            description = description,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
}
