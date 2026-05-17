package com.dox.adapter.out.billing

import com.dox.application.port.output.AsaasCustomerDetails
import com.dox.application.port.output.AsaasCustomerResult
import com.dox.application.port.output.AsaasPaymentSnapshot
import com.dox.application.port.output.AsaasSubscriptionResult
import com.dox.application.port.output.BillingPort
import com.dox.application.port.output.CreateAsaasCustomerCommand
import com.dox.application.port.output.CreateAsaasSubscriptionCommand
import com.dox.application.port.output.CreateOneTimePaymentCommand
import com.dox.application.port.output.TokenizeCardCommand
import com.dox.application.port.output.TokenizedCardResult
import com.dox.application.port.output.UpdateAsaasCustomerCommand
import com.dox.application.port.output.UpdateAsaasSubscriptionCommand
import com.dox.domain.billing.BillingType
import com.dox.domain.exception.BusinessException
import com.dox.extensions.sanitizeDocument
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
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
            customerBody(
                name = command.name,
                cpfCnpj = command.cpfCnpj,
                email = command.email,
                mobilePhone = command.mobilePhone,
                postalCode = command.postalCode,
                address = command.address,
                addressNumber = command.addressNumber,
                complement = command.complement,
                province = command.province,
            )
        val response =
            client.post()
                .uri("/customers")
                .body(body)
                .retrieve()
                .body(Map::class.java) ?: throw BusinessException("Asaas /customers retornou vazio")
        val id = response["id"] as? String ?: throw BusinessException("Asaas /customers sem id na resposta")
        return AsaasCustomerResult(asaasCustomerId = id)
    }

    override fun getCustomer(asaasCustomerId: String): AsaasCustomerDetails {
        val response =
            client.get()
                .uri("/customers/{id}", asaasCustomerId)
                .retrieve()
                .body(Map::class.java) ?: throw BusinessException("Asaas GET /customers/$asaasCustomerId retornou vazio")
        return AsaasCustomerDetails(
            asaasCustomerId = response["id"] as? String ?: asaasCustomerId,
            name = (response["name"] as? String) ?: "",
            email = response["email"] as? String,
            cpfCnpj = (response["cpfCnpj"] as? String) ?: "",
            mobilePhone = response["mobilePhone"] as? String,
            postalCode = response["postalCode"] as? String,
            address = response["address"] as? String,
            addressNumber = response["addressNumber"] as? String,
            complement = response["complement"] as? String,
            province = response["province"] as? String,
        )
    }

    override fun updateCustomer(command: UpdateAsaasCustomerCommand) {
        val body =
            customerBody(
                name = command.name,
                cpfCnpj = command.cpfCnpj,
                email = command.email,
                mobilePhone = command.mobilePhone,
                postalCode = command.postalCode,
                address = command.address,
                addressNumber = command.addressNumber,
                complement = command.complement,
                province = command.province,
            )
        client.post()
            .uri("/customers/{id}", command.asaasCustomerId)
            .body(body)
            .retrieve()
            .body(Map::class.java) ?: throw BusinessException("Asaas update /customers/${command.asaasCustomerId} retornou vazio")
    }

    private fun customerBody(
        name: String,
        cpfCnpj: String,
        email: String?,
        mobilePhone: String?,
        postalCode: String?,
        address: String?,
        addressNumber: String?,
        complement: String?,
        province: String?,
    ): Map<String, String?> =
        mapOf(
            "name" to name,
            "cpfCnpj" to cpfCnpj.sanitizeDocument(),
            "email" to email,
            "mobilePhone" to mobilePhone?.filter { it.isDigit() }?.takeIf { it.isNotBlank() },
            "postalCode" to postalCode?.filter { it.isDigit() }?.takeIf { it.isNotBlank() },
            "address" to address?.takeIf { it.isNotBlank() },
            "addressNumber" to addressNumber?.takeIf { it.isNotBlank() },
            "complement" to complement?.takeIf { it.isNotBlank() },
            "province" to province?.takeIf { it.isNotBlank() },
        ).filterValues { it != null }

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

    override fun tokenizeCard(command: TokenizeCardCommand): TokenizedCardResult {
        val body =
            mapOf(
                "customer" to command.asaasCustomerId,
                "creditCard" to
                    mapOf(
                        "holderName" to command.holderName,
                        "number" to command.number.filter { it.isDigit() },
                        "expiryMonth" to command.expiryMonth,
                        "expiryYear" to command.expiryYear,
                        "ccv" to command.ccv,
                    ),
                "creditCardHolderInfo" to
                    mapOf(
                        "name" to command.holderInfo.name,
                        "email" to command.holderInfo.email,
                        "cpfCnpj" to command.holderInfo.cpfCnpj.sanitizeDocument(),
                        "postalCode" to command.holderInfo.postalCode.filter { it.isDigit() },
                        "addressNumber" to command.holderInfo.addressNumber,
                        "addressComplement" to command.holderInfo.addressComplement,
                        "phone" to command.holderInfo.phone,
                        "mobilePhone" to command.holderInfo.mobilePhone,
                    ).filterValues { it != null },
                "remoteIp" to command.remoteIp,
            )
        val response =
            runCatching {
                client.post()
                    .uri("/creditCard/tokenize")
                    .body(body)
                    .retrieve()
                    .body(Map::class.java)
            }.getOrElse { throw mapAsaasTokenizeError(it) }
                ?: throw BusinessException("Asaas /creditCard/tokenize retornou vazio")
        val token = response["creditCardToken"] as? String ?: throw BusinessException("Asaas tokenize sem creditCardToken")
        val brand = response["creditCardBrand"] as? String ?: "UNKNOWN"
        val last4 = (response["creditCardNumber"] as? String)?.takeLast(4) ?: ""
        return TokenizedCardResult(creditCardToken = token, brand = brand, last4 = last4)
    }

    private fun mapAsaasTokenizeError(ex: Throwable): BusinessException {
        if (ex !is HttpClientErrorException) return BusinessException("Não foi possível validar o cartão. Tente novamente em alguns instantes.")
        val code = extractAsaasErrorCode(ex)
        val message =
            when (code) {
                "invalid_creditCard" -> "Bandeira não suportada. Tente Visa, Mastercard, Amex, Elo, Diners ou Discover."
                "invalid_creditCardNumber" -> "Número do cartão inválido. Verifique e tente novamente."
                "invalid_creditCardCcv" -> "CCV inválido. Confira o código de segurança do cartão."
                "invalid_creditCardExpiry" -> "Validade do cartão inválida ou expirada."
                "invalid_holderInfo" -> "Dados do titular incompletos ou inválidos."
                "invalid_creditCardHolderName" -> "Nome do titular inválido."
                else -> "O cartão não pôde ser validado pela operadora. Tente outro cartão ou revise os dados."
            }
        return BusinessException(message)
    }

    private fun extractAsaasErrorCode(ex: HttpClientErrorException): String? =
        runCatching {
            @Suppress("UNCHECKED_CAST")
            val body = ex.getResponseBodyAs(Map::class.java) as? Map<String, Any?>
            val errors = body?.get("errors") as? List<Map<String, Any?>>
            errors?.firstOrNull()?.get("code") as? String
        }.getOrNull()

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
