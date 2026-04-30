package com.dox.adapter.out.billing

import com.dox.application.port.output.InvoiceIssuerPort
import com.dox.application.port.output.IssueInvoiceCommand
import com.dox.application.port.output.IssueInvoiceResult
import com.dox.domain.exception.BusinessException
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class AsaasNfseAdapter(
    private val properties: AsaasProperties,
) : InvoiceIssuerPort {
    private val client: RestClient =
        RestClient.builder()
            .baseUrl(properties.baseUrl)
            .defaultHeader("access_token", properties.apiKey)
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
            .build()

    override fun isEnabled(): Boolean = properties.nfse.enabled

    override fun issueInvoice(command: IssueInvoiceCommand): IssueInvoiceResult {
        val nfse = properties.nfse
        val description = command.serviceDescription ?: nfse.serviceDescription
        val body =
            mutableMapOf<String, Any?>(
                "payment" to command.asaasPaymentId,
                "serviceDescription" to description,
                "taxes" to
                    mapOf(
                        "retainIss" to nfse.retainIss,
                        "iss" to nfse.issPercentage,
                    ),
            )
        if (nfse.municipalServiceId.isNotBlank()) body["municipalServiceId"] = nfse.municipalServiceId
        if (nfse.municipalServiceCode.isNotBlank()) body["municipalServiceCode"] = nfse.municipalServiceCode
        if (nfse.municipalServiceName.isNotBlank()) body["municipalServiceName"] = nfse.municipalServiceName

        val response =
            client.post()
                .uri("/invoices")
                .body(body)
                .retrieve()
                .body(Map::class.java) ?: throw BusinessException("Asaas /invoices retornou vazio")
        val id = response["id"] as? String ?: throw BusinessException("Asaas /invoices sem id na resposta")
        return IssueInvoiceResult(
            asaasInvoiceId = id,
            status = response["status"] as? String ?: "PENDING",
            pdfUrl = response["pdfUrl"] as? String,
            xmlUrl = response["xmlUrl"] as? String,
        )
    }
}
