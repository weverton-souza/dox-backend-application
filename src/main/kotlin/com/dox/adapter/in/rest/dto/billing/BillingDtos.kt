package com.dox.adapter.`in`.rest.dto.billing

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class SubscribeBundleRequest(
    @field:NotBlank
    val bundleId: String,
    @field:NotBlank
    val cycle: String,
    @field:NotBlank
    val billingType: String,
    @field:NotBlank
    @field:Size(max = 255)
    val customerName: String,
    @field:NotBlank
    @field:Size(max = 20)
    val customerCpfCnpj: String,
    @field:Email
    val customerEmail: String? = null,
    val creditCardToken: String? = null,
)

data class SubscribeModulesRequest(
    val moduleIds: List<String>,
    @field:NotBlank
    val cycle: String,
    @field:NotBlank
    val billingType: String,
    @field:NotBlank
    @field:Size(max = 255)
    val customerName: String,
    @field:NotBlank
    @field:Size(max = 20)
    val customerCpfCnpj: String,
    @field:Email
    val customerEmail: String? = null,
    val creditCardToken: String? = null,
)

data class AddOrRemoveModuleRequest(
    @field:NotBlank
    val moduleId: String,
)

data class CancelSubscriptionRequest(
    val reason: String? = null,
)

data class SubscriptionResponse(
    val id: UUID,
    val status: String,
    val billingCycle: String,
    val billingType: String,
    val valueCents: Int,
    val currentPeriodStart: LocalDateTime?,
    val currentPeriodEnd: LocalDateTime?,
    val nextDueDate: LocalDate?,
    val trialEnd: LocalDateTime?,
    val canceledAt: LocalDateTime?,
    val cancelEffectiveAt: LocalDateTime?,
    val cancelReason: String?,
)

data class PaymentResponse(
    val id: UUID,
    val asaasPaymentId: String,
    val amountCents: Int,
    val status: String,
    val billingType: String,
    val dueDate: LocalDate,
    val paidAt: LocalDateTime?,
    val refundedAt: LocalDateTime?,
    val invoiceUrl: String?,
    val bankSlipUrl: String?,
    val pixQrCode: String?,
    val pixCopyPaste: String?,
    val description: String?,
)

data class InvoiceResponse(
    val id: UUID,
    val paymentId: UUID,
    val status: String,
    val pdfUrl: String?,
    val xmlUrl: String?,
    val issuedAt: LocalDateTime?,
)

data class PriceBreakdownResponse(
    val basePriceCents: Int,
    val bundleDiscountCents: Int,
    val finalPriceCents: Int,
    val cycle: String,
)
