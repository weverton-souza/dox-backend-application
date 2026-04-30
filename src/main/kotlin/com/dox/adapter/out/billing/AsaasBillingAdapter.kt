package com.dox.adapter.out.billing

import com.dox.application.port.output.AsaasCustomerResult
import com.dox.application.port.output.AsaasPaymentSnapshot
import com.dox.application.port.output.AsaasSubscriptionResult
import com.dox.application.port.output.BillingPort
import com.dox.application.port.output.CreateAsaasCustomerCommand
import com.dox.application.port.output.CreateAsaasSubscriptionCommand
import com.dox.application.port.output.CreateOneTimePaymentCommand
import com.dox.application.port.output.UpdateAsaasSubscriptionCommand
import com.dox.domain.billing.BillingType
import com.dox.domain.exception.BusinessException
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

@Configuration
@EnableConfigurationProperties(AsaasProperties::class)
class AsaasConfiguration

@Component
class AsaasBillingAdapter(
    private val properties: AsaasProperties,
) : BillingPort {
    private val client: RestClient =
        RestClient.builder()
            .baseUrl(properties.baseUrl)
            .defaultHeader("access_token", properties.apiKey)
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
            .build()

    override fun createCustomer(command: CreateAsaasCustomerCommand): AsaasCustomerResult {
        val body =
            mapOf(
                "name" to command.name,
                "cpfCnpj" to command.cpfCnpj.filter { it.isDigit() },
                "email" to command.email,
            ).filterValues { it != null }
        val response =
            client.post()
                .uri("/customers")
                .body(body)
                .retrieve()
                .body(Map::class.java) ?: throw BusinessException("Asaas /customers retornou vazio")
        val id = response["id"] as? String ?: throw BusinessException("Asaas /customers sem id na resposta")
        return AsaasCustomerResult(asaasCustomerId = id)
    }

    override fun createSubscription(command: CreateAsaasSubscriptionCommand): AsaasSubscriptionResult {
        val body =
            mapOf(
                "customer" to command.asaasCustomerId,
                "billingType" to mapBillingType(command.billingType),
                "cycle" to command.cycle.asaasValue,
                "nextDueDate" to command.nextDueDate.toString(),
                "value" to centsToDecimal(command.valueCents),
                "description" to command.description,
                "creditCardToken" to command.creditCardToken,
            ).filterValues { it != null }
        val response =
            client.post()
                .uri("/subscriptions")
                .body(body)
                .retrieve()
                .body(Map::class.java) ?: throw BusinessException("Asaas /subscriptions retornou vazio")
        val id = response["id"] as? String ?: throw BusinessException("Asaas /subscriptions sem id na resposta")
        val nextDue = (response["nextDueDate"] as? String)?.let { LocalDate.parse(it) } ?: command.nextDueDate
        return AsaasSubscriptionResult(asaasSubscriptionId = id, nextDueDate = nextDue)
    }

    override fun updateSubscription(command: UpdateAsaasSubscriptionCommand) {
        val body =
            mutableMapOf<String, Any?>().apply {
                command.newValueCents?.let { put("value", centsToDecimal(it)) }
                command.newDueDate?.let { put("nextDueDate", it.toString()) }
                command.newCycle?.let { put("cycle", it.asaasValue) }
                if (command.updatePendingPayments) put("updatePendingPayments", true)
            }
        if (body.isEmpty()) return
        client.post()
            .uri("/subscriptions/{id}", command.asaasSubscriptionId)
            .body(body)
            .retrieve()
            .toBodilessEntity()
    }

    override fun cancelSubscription(asaasSubscriptionId: String) {
        client.delete()
            .uri("/subscriptions/{id}", asaasSubscriptionId)
            .retrieve()
            .toBodilessEntity()
    }

    override fun createOneTimePayment(command: CreateOneTimePaymentCommand): AsaasPaymentSnapshot {
        val body =
            mapOf(
                "customer" to command.asaasCustomerId,
                "billingType" to mapBillingType(command.billingType),
                "value" to centsToDecimal(command.valueCents),
                "dueDate" to command.dueDate.toString(),
                "description" to command.description,
            ).filterValues { it != null }
        val response =
            client.post()
                .uri("/payments")
                .body(body)
                .retrieve()
                .body(Map::class.java) ?: throw BusinessException("Asaas /payments retornou vazio")
        return mapPaymentSnapshot(response)
    }

    override fun getPayment(asaasPaymentId: String): AsaasPaymentSnapshot {
        val response =
            client.get()
                .uri("/payments/{id}", asaasPaymentId)
                .retrieve()
                .body(Map::class.java) ?: throw BusinessException("Asaas GET /payments/$asaasPaymentId retornou vazio")
        return mapPaymentSnapshot(response)
    }

    private fun mapPaymentSnapshot(response: Map<*, *>): AsaasPaymentSnapshot {
        val id = response["id"] as? String ?: throw BusinessException("Asaas payment sem id")
        val status = response["status"] as? String ?: "UNKNOWN"
        val billingTypeStr = response["billingType"] as? String ?: "UNDEFINED"
        val value = (response["value"] as? Number)?.toDouble() ?: 0.0
        val dueDate = (response["dueDate"] as? String)?.let { LocalDate.parse(it) } ?: LocalDate.now()
        return AsaasPaymentSnapshot(
            asaasPaymentId = id,
            status = status,
            billingType = parseBillingType(billingTypeStr),
            valueCents = (value * 100).toInt(),
            dueDate = dueDate,
            invoiceUrl = response["invoiceUrl"] as? String,
            bankSlipUrl = response["bankSlipUrl"] as? String,
            pixQrCode = response["pixQrCode"] as? String,
            pixCopyPaste = response["pixCopyPaste"] as? String,
        )
    }

    private fun mapBillingType(type: BillingType): String =
        when (type) {
            BillingType.BOLETO -> "BOLETO"
            BillingType.PIX -> "PIX"
            BillingType.CREDIT_CARD -> "CREDIT_CARD"
            BillingType.UNDEFINED -> "UNDEFINED"
        }

    private fun parseBillingType(value: String): BillingType = runCatching { BillingType.valueOf(value) }.getOrDefault(BillingType.UNDEFINED)

    private fun centsToDecimal(cents: Int): BigDecimal = BigDecimal(cents).divide(BigDecimal(100), 2, RoundingMode.HALF_UP)
}
