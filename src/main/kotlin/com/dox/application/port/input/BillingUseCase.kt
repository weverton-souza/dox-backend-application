package com.dox.application.port.input

import com.dox.domain.billing.BillingCycle
import com.dox.domain.billing.BillingType
import com.dox.domain.billing.NfseInvoice
import com.dox.domain.billing.Payment
import com.dox.domain.billing.PriceBreakdown
import com.dox.domain.billing.Subscription
import java.time.LocalDate
import java.util.UUID

data class SubscribeBundleCommand(
    val tenantId: UUID,
    val bundleId: String,
    val cycle: BillingCycle,
    val billingType: BillingType,
    val customerName: String,
    val customerCpfCnpj: String,
    val customerEmail: String? = null,
    val creditCardToken: String? = null,
)

data class SubscribeModulesCommand(
    val tenantId: UUID,
    val moduleIds: List<String>,
    val cycle: BillingCycle,
    val billingType: BillingType,
    val customerName: String,
    val customerCpfCnpj: String,
    val customerEmail: String? = null,
    val creditCardToken: String? = null,
)

data class CancelSubscriptionCommand(
    val tenantId: UUID,
    val reason: String? = null,
)

data class TokenizeCreditCardCommand(
    val tenantId: UUID,
    val cardHolderName: String,
    val cardNumber: String,
    val cardExpiryMonth: String,
    val cardExpiryYear: String,
    val cardCcv: String,
    val billingName: String,
    val billingEmail: String,
    val billingCpfCnpj: String,
    val billingPostalCode: String,
    val billingAddressNumber: String,
    val billingAddressComplement: String? = null,
    val billingPhone: String? = null,
    val billingMobilePhone: String? = null,
    val remoteIp: String,
    val makeDefault: Boolean = false,
)

data class TokenizedCard(
    val token: String,
    val brand: String,
    val last4: String,
)

interface BillingUseCase {
    fun subscribeBundle(command: SubscribeBundleCommand): Subscription

    fun subscribeModules(command: SubscribeModulesCommand): Subscription

    fun addModule(
        tenantId: UUID,
        moduleId: String,
    ): Subscription

    fun removeModule(
        tenantId: UUID,
        moduleId: String,
    ): Subscription

    fun cancelSubscription(command: CancelSubscriptionCommand): Subscription

    fun reactivateSubscription(tenantId: UUID): Subscription

    fun getSubscription(tenantId: UUID): Subscription?

    fun listPayments(
        tenantId: UUID,
        from: LocalDate? = null,
        to: LocalDate? = null,
    ): List<Payment>

    fun listInvoices(tenantId: UUID): List<NfseInvoice>

    fun pricePreview(
        moduleIds: List<String>,
        cycle: BillingCycle,
        bundleId: String? = null,
    ): PriceBreakdown

    fun tokenizeCreditCard(command: TokenizeCreditCardCommand): TokenizedCard
}
