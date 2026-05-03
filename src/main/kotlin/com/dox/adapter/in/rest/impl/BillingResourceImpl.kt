package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.billing.AddOrRemoveModuleRequest
import com.dox.adapter.`in`.rest.dto.billing.CancelSubscriptionRequest
import com.dox.adapter.`in`.rest.dto.billing.InvoiceResponse
import com.dox.adapter.`in`.rest.dto.billing.PaymentResponse
import com.dox.adapter.`in`.rest.dto.billing.PriceBreakdownResponse
import com.dox.adapter.`in`.rest.dto.billing.SubscribeBundleRequest
import com.dox.adapter.`in`.rest.dto.billing.SubscribeModulesRequest
import com.dox.adapter.`in`.rest.dto.billing.SubscriptionResponse
import com.dox.adapter.`in`.rest.dto.billing.TokenizeCreditCardRequest
import com.dox.adapter.`in`.rest.dto.billing.TokenizedCardResponse
import com.dox.adapter.`in`.rest.resource.BillingResource
import com.dox.application.port.input.BillingUseCase
import com.dox.application.port.input.CancelSubscriptionCommand
import com.dox.application.port.input.SubscribeBundleCommand
import com.dox.application.port.input.SubscribeModulesCommand
import com.dox.application.port.input.TokenizeCreditCardCommand
import com.dox.domain.billing.BillingCycle
import com.dox.domain.billing.BillingType
import com.dox.domain.billing.NfseInvoice
import com.dox.domain.billing.Payment
import com.dox.domain.billing.PriceBreakdown
import com.dox.domain.billing.Subscription
import com.dox.domain.exception.BusinessException
import com.dox.extensions.extractClientIp
import com.dox.shared.ContextHolder
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class BillingResourceImpl(
    private val billingUseCase: BillingUseCase,
) : BillingResource {
    override fun subscribeBundle(request: SubscribeBundleRequest): ResponseEntity<SubscriptionResponse> {
        val tenantId = ContextHolder.getTenantIdOrThrow()
        val sub =
            billingUseCase.subscribeBundle(
                SubscribeBundleCommand(
                    tenantId = tenantId,
                    bundleId = request.bundleId,
                    cycle = parseCycle(request.cycle),
                    billingType = parseBillingType(request.billingType),
                    customerName = request.customerName,
                    customerCpfCnpj = request.customerCpfCnpj,
                    customerEmail = request.customerEmail,
                    creditCardToken = request.creditCardToken,
                ),
            )
        return responseEntity(sub.toResponse())
    }

    override fun subscribeModules(request: SubscribeModulesRequest): ResponseEntity<SubscriptionResponse> {
        val tenantId = ContextHolder.getTenantIdOrThrow()
        val sub =
            billingUseCase.subscribeModules(
                SubscribeModulesCommand(
                    tenantId = tenantId,
                    moduleIds = request.moduleIds,
                    cycle = parseCycle(request.cycle),
                    billingType = parseBillingType(request.billingType),
                    customerName = request.customerName,
                    customerCpfCnpj = request.customerCpfCnpj,
                    customerEmail = request.customerEmail,
                    creditCardToken = request.creditCardToken,
                ),
            )
        return responseEntity(sub.toResponse())
    }

    override fun addModule(request: AddOrRemoveModuleRequest): ResponseEntity<SubscriptionResponse> {
        val tenantId = ContextHolder.getTenantIdOrThrow()
        return responseEntity(billingUseCase.addModule(tenantId, request.moduleId).toResponse())
    }

    override fun removeModule(request: AddOrRemoveModuleRequest): ResponseEntity<SubscriptionResponse> {
        val tenantId = ContextHolder.getTenantIdOrThrow()
        return responseEntity(billingUseCase.removeModule(tenantId, request.moduleId).toResponse())
    }

    override fun cancel(request: CancelSubscriptionRequest?): ResponseEntity<SubscriptionResponse> {
        val tenantId = ContextHolder.getTenantIdOrThrow()
        val sub =
            billingUseCase.cancelSubscription(
                CancelSubscriptionCommand(tenantId = tenantId, reason = request?.reason),
            )
        return responseEntity(sub.toResponse())
    }

    override fun reactivate(): ResponseEntity<SubscriptionResponse> {
        val tenantId = ContextHolder.getTenantIdOrThrow()
        return responseEntity(billingUseCase.reactivateSubscription(tenantId).toResponse())
    }

    override fun getSubscription(): ResponseEntity<SubscriptionResponse?> {
        val tenantId = ContextHolder.getTenantIdOrThrow()
        return responseEntity(billingUseCase.getSubscription(tenantId)?.toResponse())
    }

    override fun listPayments(
        from: LocalDate?,
        to: LocalDate?,
    ): ResponseEntity<List<PaymentResponse>> {
        val tenantId = ContextHolder.getTenantIdOrThrow()
        return responseEntity(billingUseCase.listPayments(tenantId, from, to).map { it.toResponse() })
    }

    override fun listInvoices(): ResponseEntity<List<InvoiceResponse>> {
        val tenantId = ContextHolder.getTenantIdOrThrow()
        return responseEntity(billingUseCase.listInvoices(tenantId).map { it.toResponse() })
    }

    override fun pricePreview(
        moduleIds: List<String>,
        cycle: String,
        bundleId: String?,
    ): ResponseEntity<PriceBreakdownResponse> {
        val breakdown = billingUseCase.pricePreview(moduleIds, parseCycle(cycle), bundleId)
        return responseEntity(breakdown.toResponse())
    }

    override fun tokenizeCreditCard(
        request: TokenizeCreditCardRequest,
        servletRequest: HttpServletRequest,
    ): ResponseEntity<TokenizedCardResponse> {
        val tenantId = ContextHolder.getTenantIdOrThrow()
        val tokenized =
            billingUseCase.tokenizeCreditCard(
                TokenizeCreditCardCommand(
                    tenantId = tenantId,
                    cardHolderName = request.cardHolderName,
                    cardNumber = request.cardNumber,
                    cardExpiryMonth = request.cardExpiryMonth,
                    cardExpiryYear = request.cardExpiryYear,
                    cardCcv = request.cardCcv,
                    billingName = request.billingName,
                    billingEmail = request.billingEmail,
                    billingCpfCnpj = request.billingCpfCnpj,
                    billingPostalCode = request.billingPostalCode,
                    billingAddressNumber = request.billingAddressNumber,
                    billingAddressComplement = request.billingAddressComplement,
                    billingPhone = request.billingPhone,
                    billingMobilePhone = request.billingMobilePhone,
                    remoteIp = extractRemoteIp(servletRequest),
                    makeDefault = request.makeDefault,
                ),
            )
        return responseEntity(TokenizedCardResponse(token = tokenized.token, brand = tokenized.brand, last4 = tokenized.last4))
    }

    private fun extractRemoteIp(request: HttpServletRequest): String = request.extractClientIp() ?: "0.0.0.0"

    private fun parseCycle(value: String): BillingCycle =
        runCatching { BillingCycle.valueOf(value) }
            .getOrElse { throw BusinessException("Cycle inválido: '$value'. Aceitos: MONTHLY, QUARTERLY, SEMIANNUALLY, YEARLY") }

    private fun parseBillingType(value: String): BillingType =
        runCatching { BillingType.valueOf(value) }
            .getOrElse { throw BusinessException("BillingType inválido: '$value'. Aceitos: BOLETO, PIX, CREDIT_CARD, UNDEFINED") }

    private fun Subscription.toResponse() =
        SubscriptionResponse(
            id = id,
            status = status.name,
            billingCycle = billingCycle.name,
            billingType = billingType.name,
            valueCents = valueCents,
            currentPeriodStart = currentPeriodStart,
            currentPeriodEnd = currentPeriodEnd,
            nextDueDate = nextDueDate,
            trialEnd = trialEnd,
            canceledAt = canceledAt,
            cancelEffectiveAt = cancelEffectiveAt,
            cancelReason = cancelReason,
        )

    private fun Payment.toResponse() =
        PaymentResponse(
            id = id,
            asaasPaymentId = asaasPaymentId,
            amountCents = amountCents,
            status = status,
            billingType = billingType.name,
            dueDate = dueDate,
            paidAt = paidAt,
            refundedAt = refundedAt,
            invoiceUrl = invoiceUrl,
            bankSlipUrl = bankSlipUrl,
            pixQrCode = pixQrCode,
            pixCopyPaste = pixCopyPaste,
            description = description,
        )

    private fun NfseInvoice.toResponse() =
        InvoiceResponse(
            id = id,
            paymentId = paymentId,
            status = status,
            pdfUrl = pdfUrl,
            xmlUrl = xmlUrl,
            issuedAt = issuedAt,
        )

    private fun PriceBreakdown.toResponse() =
        PriceBreakdownResponse(
            basePriceCents = basePriceCents,
            bundleDiscountCents = bundleDiscountCents,
            finalPriceCents = finalPriceCents,
            cycle = cycle.name,
        )
}
