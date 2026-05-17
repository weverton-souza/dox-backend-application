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
    @field:NotBlank
    @field:Size(min = 10, max = 20)
    val customerMobilePhone: String,
    @field:NotBlank
    @field:Size(min = 8, max = 10)
    val customerPostalCode: String,
    @field:NotBlank
    @field:Size(max = 255)
    val customerAddress: String,
    @field:NotBlank
    @field:Size(max = 20)
    val customerAddressNumber: String,
    @field:Size(max = 255)
    val customerAddressComplement: String? = null,
    @field:NotBlank
    @field:Size(max = 100)
    val customerProvince: String,
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
    @field:NotBlank
    @field:Size(min = 10, max = 20)
    val customerMobilePhone: String,
    @field:NotBlank
    @field:Size(min = 8, max = 10)
    val customerPostalCode: String,
    @field:NotBlank
    @field:Size(max = 255)
    val customerAddress: String,
    @field:NotBlank
    @field:Size(max = 20)
    val customerAddressNumber: String,
    @field:Size(max = 255)
    val customerAddressComplement: String? = null,
    @field:NotBlank
    @field:Size(max = 100)
    val customerProvince: String,
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

data class CustomerProfileResponse(
    val name: String,
    val email: String?,
    val cpfCnpj: String,
    val mobilePhone: String?,
    val postalCode: String?,
    val address: String?,
    val addressNumber: String?,
    val complement: String?,
    val province: String?,
)

data class UpdateCustomerProfileRequest(
    @field:NotBlank
    @field:Size(max = 255)
    val name: String,
    @field:Email
    val email: String? = null,
    @field:NotBlank
    @field:Size(max = 20)
    val cpfCnpj: String,
    @field:NotBlank
    @field:Size(min = 10, max = 20)
    val mobilePhone: String,
    @field:NotBlank
    @field:Size(min = 8, max = 10)
    val postalCode: String,
    @field:NotBlank
    @field:Size(max = 255)
    val address: String,
    @field:NotBlank
    @field:Size(max = 20)
    val addressNumber: String,
    @field:Size(max = 255)
    val complement: String? = null,
    @field:NotBlank
    @field:Size(max = 100)
    val province: String,
)

data class PaymentMethodCardResponse(
    val id: UUID,
    val brand: String,
    val last4: String,
    val holderName: String,
    val isDefault: Boolean,
    val expiresAt: LocalDate?,
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

data class TokenizeCreditCardRequest(
    @field:NotBlank
    @field:Size(max = 255)
    val cardHolderName: String,
    @field:NotBlank
    val cardNumber: String,
    @field:NotBlank
    @field:Size(min = 1, max = 2)
    val cardExpiryMonth: String,
    @field:NotBlank
    @field:Size(min = 4, max = 4)
    val cardExpiryYear: String,
    @field:NotBlank
    @field:Size(min = 3, max = 4)
    val cardCcv: String,
    @field:NotBlank
    @field:Size(max = 255)
    val billingName: String,
    @field:NotBlank
    @field:Email
    val billingEmail: String,
    @field:NotBlank
    @field:Size(max = 20)
    val billingCpfCnpj: String,
    @field:NotBlank
    @field:Size(max = 10)
    val billingPostalCode: String,
    @field:NotBlank
    @field:Size(max = 20)
    val billingAddressNumber: String,
    @field:Size(max = 100)
    val billingAddressComplement: String? = null,
    @field:Size(max = 20)
    val billingPhone: String? = null,
    @field:Size(max = 20)
    val billingMobilePhone: String? = null,
    val makeDefault: Boolean = false,
)

data class TokenizedCardResponse(
    val token: String,
    val brand: String,
    val last4: String,
)
