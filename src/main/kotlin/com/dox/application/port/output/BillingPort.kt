package com.dox.application.port.output

import com.dox.domain.billing.BillingCycle
import com.dox.domain.billing.BillingType
import java.time.LocalDate

data class CreateAsaasCustomerCommand(
    val name: String,
    val email: String?,
    val cpfCnpj: String,
)

data class AsaasCustomerResult(
    val asaasCustomerId: String,
)

data class CreateAsaasSubscriptionCommand(
    val asaasCustomerId: String,
    val billingType: BillingType,
    val cycle: BillingCycle,
    val nextDueDate: LocalDate,
    val valueCents: Int,
    val description: String?,
    val creditCardToken: String? = null,
)

data class AsaasSubscriptionResult(
    val asaasSubscriptionId: String,
    val nextDueDate: LocalDate,
)

data class UpdateAsaasSubscriptionCommand(
    val asaasSubscriptionId: String,
    val newValueCents: Int? = null,
    val newDueDate: LocalDate? = null,
    val newCycle: BillingCycle? = null,
    val updatePendingPayments: Boolean = false,
)

data class CreateOneTimePaymentCommand(
    val asaasCustomerId: String,
    val billingType: BillingType,
    val valueCents: Int,
    val dueDate: LocalDate,
    val description: String?,
)

data class AsaasPaymentSnapshot(
    val asaasPaymentId: String,
    val status: String,
    val billingType: BillingType,
    val valueCents: Int,
    val dueDate: LocalDate,
    val invoiceUrl: String?,
    val bankSlipUrl: String?,
    val pixQrCode: String?,
    val pixCopyPaste: String?,
)

interface BillingPort {
    fun createCustomer(command: CreateAsaasCustomerCommand): AsaasCustomerResult

    fun createSubscription(command: CreateAsaasSubscriptionCommand): AsaasSubscriptionResult

    fun updateSubscription(command: UpdateAsaasSubscriptionCommand)

    fun cancelSubscription(asaasSubscriptionId: String)

    fun createOneTimePayment(command: CreateOneTimePaymentCommand): AsaasPaymentSnapshot

    fun getPayment(asaasPaymentId: String): AsaasPaymentSnapshot
}
