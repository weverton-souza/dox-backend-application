package com.dox.application.service

import com.dox.adapter.out.billing.AsaasProperties
import com.dox.application.port.input.WebhookUseCase
import com.dox.application.port.output.InvoiceIssuerPort
import com.dox.application.port.output.IssueInvoiceCommand
import com.dox.application.port.output.NfseInvoicePersistencePort
import com.dox.application.port.output.PaymentPersistencePort
import com.dox.application.port.output.ProcessedWebhookPersistencePort
import com.dox.application.port.output.SubscriptionPersistencePort
import com.dox.domain.billing.BillingType
import com.dox.domain.billing.NfseInvoice
import com.dox.domain.billing.Payment
import com.dox.domain.billing.SubscriptionEvent
import com.dox.domain.billing.SubscriptionStateMachine
import com.dox.domain.exception.AccessDeniedException
import com.dox.domain.exception.BusinessException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class WebhookServiceImpl(
    private val asaasProperties: AsaasProperties,
    private val processedWebhookPort: ProcessedWebhookPersistencePort,
    private val paymentPort: PaymentPersistencePort,
    private val subscriptionPort: SubscriptionPersistencePort,
    private val invoiceIssuerPort: InvoiceIssuerPort,
    private val invoicePort: NfseInvoicePersistencePort,
) : WebhookUseCase {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun handleAsaasEvent(
        receivedToken: String?,
        payload: Map<String, Any?>,
    ) {
        validateToken(receivedToken)
        val eventId = payload["id"] as? String ?: throw BusinessException("Webhook sem 'id'")
        val eventType = payload["event"] as? String ?: throw BusinessException("Webhook sem 'event'")

        if (processedWebhookPort.isProcessed(eventId)) {
            log.info("Webhook {} já processado (idempotência)", eventId)
            return
        }

        when (eventType) {
            "PAYMENT_CREATED", "PAYMENT_UPDATED" -> upsertPayment(payload)
            "PAYMENT_CONFIRMED", "PAYMENT_RECEIVED" -> markPaymentReceived(payload)
            "PAYMENT_OVERDUE" -> markPaymentOverdue(payload)
            "PAYMENT_REFUNDED", "PAYMENT_PARTIALLY_REFUNDED" -> markPaymentRefunded(payload)
            "PAYMENT_DELETED" -> upsertPayment(payload)
            "SUBSCRIPTION_INACTIVATED" -> finalizeSubscription(payload)
            else -> log.info("Webhook event '{}' ignorado", eventType)
        }

        processedWebhookPort.markProcessed(eventId, eventType, payload)
    }

    private fun validateToken(received: String?) {
        val expected = asaasProperties.webhookToken
        if (expected.isBlank()) return
        if (received != expected) throw AccessDeniedException("Webhook token inválido")
    }

    private fun upsertPayment(payload: Map<String, Any?>) {
        val paymentMap = payload["payment"] as? Map<*, *> ?: return
        val asaasPaymentId = paymentMap["id"] as? String ?: return
        val asaasSubscriptionId = paymentMap["subscription"] as? String

        val subscription = asaasSubscriptionId?.let { subscriptionPort.findByAsaasSubscriptionId(it) }
        val tenantId = subscription?.tenantId ?: return

        val existing = paymentPort.findByAsaasPaymentId(asaasPaymentId)
        val billingType =
            runCatching { BillingType.valueOf((paymentMap["billingType"] as? String) ?: "UNDEFINED") }
                .getOrDefault(BillingType.UNDEFINED)
        val amountCents = ((paymentMap["value"] as? Number)?.toDouble()?.times(100))?.toInt() ?: existing?.amountCents ?: 0
        val dueDate = (paymentMap["dueDate"] as? String)?.let { LocalDate.parse(it) } ?: existing?.dueDate ?: LocalDate.now()

        val payment =
            existing?.copy(
                status = paymentMap["status"] as? String ?: existing.status,
                amountCents = amountCents,
                dueDate = dueDate,
                invoiceUrl = paymentMap["invoiceUrl"] as? String ?: existing.invoiceUrl,
                bankSlipUrl = paymentMap["bankSlipUrl"] as? String ?: existing.bankSlipUrl,
                pixQrCode = paymentMap["pixQrCode"] as? String ?: existing.pixQrCode,
                pixCopyPaste = paymentMap["pixCopyPaste"] as? String ?: existing.pixCopyPaste,
                description = paymentMap["description"] as? String ?: existing.description,
            ) ?: Payment(
                tenantId = tenantId,
                subscriptionId = subscription.id,
                asaasPaymentId = asaasPaymentId,
                amountCents = amountCents,
                status = paymentMap["status"] as? String ?: "PENDING",
                billingType = billingType,
                dueDate = dueDate,
                invoiceUrl = paymentMap["invoiceUrl"] as? String,
                bankSlipUrl = paymentMap["bankSlipUrl"] as? String,
                pixQrCode = paymentMap["pixQrCode"] as? String,
                pixCopyPaste = paymentMap["pixCopyPaste"] as? String,
                description = paymentMap["description"] as? String,
            )
        paymentPort.save(payment)
    }

    private fun markPaymentReceived(payload: Map<String, Any?>) {
        val paymentMap = payload["payment"] as? Map<*, *> ?: return
        val asaasPaymentId = paymentMap["id"] as? String ?: return
        val existing = paymentPort.findByAsaasPaymentId(asaasPaymentId)
        val savedPayment =
            existing?.let {
                paymentPort.save(it.copy(status = "RECEIVED", paidAt = LocalDateTime.now()))
            }
        val asaasSubscriptionId = paymentMap["subscription"] as? String ?: return
        val subscription = subscriptionPort.findByAsaasSubscriptionId(asaasSubscriptionId) ?: return
        if (SubscriptionStateMachine.canTransition(subscription.status, SubscriptionEvent.PAYMENT_SUCCEEDED)) {
            val newStatus = SubscriptionStateMachine.transition(subscription.status, SubscriptionEvent.PAYMENT_SUCCEEDED)
            subscriptionPort.save(subscription.copy(status = newStatus))
        }
        if (savedPayment != null) {
            issueInvoiceFor(savedPayment)
        }
    }

    private fun issueInvoiceFor(payment: Payment) {
        if (!invoiceIssuerPort.isEnabled()) {
            log.debug("NFS-e desabilitada — pulando emissão para payment {}", payment.id)
            return
        }
        val existingInvoice = invoicePort.findByPaymentId(payment.id)
        if (existingInvoice != null && existingInvoice.status != "ERROR") {
            log.debug("NFS-e já existente para payment {} (status={})", payment.id, existingInvoice.status)
            return
        }
        runCatching {
            invoiceIssuerPort.issueInvoice(IssueInvoiceCommand(asaasPaymentId = payment.asaasPaymentId))
        }.onSuccess { result ->
            invoicePort.save(
                NfseInvoice(
                    tenantId = payment.tenantId,
                    paymentId = payment.id,
                    asaasInvoiceId = result.asaasInvoiceId,
                    status = result.status,
                    pdfUrl = result.pdfUrl,
                    xmlUrl = result.xmlUrl,
                    issuedAt = LocalDateTime.now(),
                ),
            )
        }.onFailure { ex ->
            log.warn("Falha ao emitir NFS-e para payment {}: {}", payment.id, ex.message)
            invoicePort.save(
                NfseInvoice(
                    tenantId = payment.tenantId,
                    paymentId = payment.id,
                    status = "ERROR",
                    error = ex.message?.take(2000),
                ),
            )
        }
    }

    private fun markPaymentOverdue(payload: Map<String, Any?>) {
        val paymentMap = payload["payment"] as? Map<*, *> ?: return
        val asaasSubscriptionId = paymentMap["subscription"] as? String ?: return
        val subscription = subscriptionPort.findByAsaasSubscriptionId(asaasSubscriptionId) ?: return
        if (SubscriptionStateMachine.canTransition(subscription.status, SubscriptionEvent.PAYMENT_FAILED)) {
            val newStatus = SubscriptionStateMachine.transition(subscription.status, SubscriptionEvent.PAYMENT_FAILED)
            subscriptionPort.save(subscription.copy(status = newStatus))
        }
    }

    private fun markPaymentRefunded(payload: Map<String, Any?>) {
        val paymentMap = payload["payment"] as? Map<*, *> ?: return
        val asaasPaymentId = paymentMap["id"] as? String ?: return
        val existing = paymentPort.findByAsaasPaymentId(asaasPaymentId) ?: return
        paymentPort.save(
            existing.copy(
                status = "REFUNDED",
                refundedAt = LocalDateTime.now(),
            ),
        )
    }

    private fun finalizeSubscription(payload: Map<String, Any?>) {
        val subscriptionMap = payload["subscription"] as? Map<*, *> ?: return
        val asaasSubscriptionId = subscriptionMap["id"] as? String ?: return
        val subscription = subscriptionPort.findByAsaasSubscriptionId(asaasSubscriptionId) ?: return
        if (SubscriptionStateMachine.canTransition(subscription.status, SubscriptionEvent.PERIOD_ENDED)) {
            val newStatus = SubscriptionStateMachine.transition(subscription.status, SubscriptionEvent.PERIOD_ENDED)
            subscriptionPort.save(subscription.copy(status = newStatus))
        }
    }
}
